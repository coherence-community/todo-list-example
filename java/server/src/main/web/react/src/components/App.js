/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

import React from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'
import * as TodoActions from '../actions/TodoActions'
import Header from '../components/Header'
import TodoInput from '../components/TodoInput'
import MainSection from '../components/MainSection'

let initialized = false;

function init(actions) {
    actions.fetchAllTodos();

    // register for SSE
    let source = new EventSource('/api/tasks/events');

    source.addEventListener("insert", (e) => {
      let todo = JSON.parse(e.data);
      actions.addTodo(todo.id, todo.createdAt, todo.description);
    });

    source.addEventListener("update", (e) => {
      let todo = JSON.parse(e.data);
      actions.updateTodo(todo.id, todo.description, todo.completed);
    });

    source.addEventListener("delete", (e) => {
      let todo = JSON.parse(e.data);
      actions.deleteTodo(todo.id);
    });

    source.addEventListener("begin", (e) => {
      console.log("listening for events from: " + e.data);
    });

    source.addEventListener("end", (e) => {
      console.log("end");
      source.close();
    });

    initialized = true;
}

const App = ({todos, actions}) => {
    if (!initialized) {
      init(actions);
    }

    return (
        <div>
        <Header />
        <TodoInput addTodo={actions.addTodoRequest}/>
        <MainSection todos={todos} actions={actions}/>
        </div>
    )
};

App.propTypes = {
  todos:   PropTypes.array.isRequired,
  actions: PropTypes.object.isRequired
};

const mapStateToProps = state => ({
  todos: state.todos
});

const mapDispatchToProps = dispatch => ({
  actions: {
    // local actions
    addTodo:               (id, createdAt, description) => { dispatch(TodoActions.addTodo(id, createdAt, description)) },
    updateTodo:            (id, description, completed) => {
      dispatch(TodoActions.updateTodo(id, description));
      dispatch(TodoActions.completeTodo(id, completed));
    },
    deleteTodo:            (id)              => { dispatch(TodoActions.deleteTodo(id)) },

    // remote actions
    fetchAllTodos:         ()                => { dispatch(TodoActions.fetchAllTodos()) },
    addTodoRequest:        (description)     => { dispatch(TodoActions.addTodoRequest(description)) },
    updateTodoRequest:     (id, description) => { dispatch(TodoActions.updateTodoRequest(id, description)) },
    deleteTodoRequest:     (id)              => { dispatch(TodoActions.deleteTodoRequest(id)) },
    toggleTodoRequest:     (id, completed)   => { dispatch(TodoActions.toggleTodoRequest(id, completed)) },
    clearCompletedRequest: ()                => { dispatch(TodoActions.clearCompletedRequest()) }
  }
});

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(App)
