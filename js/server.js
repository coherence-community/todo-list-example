/*
 *Copyright (c) 2020 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

const express = require('express')
const coh = require('@oracle/coherence')
const path = require('path')
const uuid = require('uuid')
const {sse} = require('@toverux/expresse')

// aliases
const Processors = coh.Processors
const Filters = coh.Filters
const Session = coh.Session
const MapListener = coh.event.MapListener

// setup Express
const port = process.env.PORT || 5000
const api = express()
api.use(express.json())
api.use(express.static(path.join(__dirname, '../java/server/src/main/web/react/build'))) // serve the react application


// setup session to Coherence
const session = new Session()
const tasks = session.getMap('tasks')

// ----- REST API -----------------------------------------------------------

/**
 * Registers a MapListener to be notified of cache events which will be pumped
 * to the front-end via SSE.
 */
api.get('/api/tasks/events', sse(), (req, res, next) => {
  const listener = new MapListener()
  listener.on('insert', data => {
    res.sse.event('insert', data.newValue)
  }).on('update', data => {
    res.sse.event('update', data.newValue)
  }).on('delete', data => {
    res.sse.event('delete', data.oldValue)
  })

  tasks.addMapListener(listener)
})

/**
 * Returns all of the current tasks unless the <em>completed</em>
 * query parameter is included in which case the tasks will be filtered
 * based on the parameter value.
 */
api.get('/api/tasks', (req, res, next) => {
  const completed = req.query.completed
  const filter = completed
      ? Filters.equal('completed', completed)
      : Filters.always()

  const toSend = []
  tasks.values(filter)
      .then(async values => {
        // copy values to array to be sent via express
        for await (let value of values) {
          toSend.push(value)
        }
        res.send(toSend)
      })
      .catch(err => next(err))
})

/**
 * Creates a new task.
 */
api.post('/api/tasks', (req, res, next) => {
  const todo = createTask(req.body.description)
  const id = todo.id
  tasks.set(id, todo)
      .then(() => {
        res.send(JSON.stringify(todo))
      })
      .catch(err => next(err))
})

/**
 * Deletes the task associated with the specified <em>id</em>.
 */
api.delete('/api/tasks/:id', (req, res, next) => {
  tasks.delete(req.params.id)
      .then(oldValue => { res.sendStatus(oldValue ? 200 : 404)})
      .catch(err => next(err))

})

/**
 * Deletes all completed tasks.
 */
api.delete('/api/tasks', (req, res, next) => {
  tasks.invokeAll(Filters.equal('completed', true),
      Processors.conditionalRemove(Filters.always()))
      .catch(err => next(err))
})

/**
 * Updates an existing task in-place within Coherence.
 */
api.put('/api/tasks/:id', (req, res, next) => {
  const description = req.body.description
  const completed = req.body.completed

  let processor = null

  if (description) {
    processor = Processors.update('description', description)
  }
  if (typeof(completed) !== 'undefined') {
    const compProcessor = Processors.update('completed', completed)
    processor = processor ? processor.andThen(compProcessor) : compProcessor
  }

  tasks.invoke(req.params.id, processor)
      .then(result => res.send(result))
      .catch(err => next(err))
})

api.listen(port, () => console.log(`Listening on port ${port}`))

// ----- helpers ------------------------------------------------------------

function createTask(description) {
  const task = {
    '@class': 'Task',
    id: uuid.v4().substr(0, 6),
    createdAt: Date.now(),
    description: description,
    completed: false
  }

  return task
}