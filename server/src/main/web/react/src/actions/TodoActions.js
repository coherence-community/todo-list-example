/*
 Copyright (c) 2020 Oracle and/or its affiliates.

 Licensed under the Universal Permissive License v 1.0 as shown at
 http://oss.oracle.com/licenses/upl.
*/
import * as types from '../constants/ActionTypes'
import request from 'superagent'

export const initTodos      = (todos)                      => ({type: types.INIT_TODOS, todos});
export const addTodo        = (id, createdAt, description) => ({type: types.ADD_TODO, id, createdAt, description});
export const deleteTodo     = (id)                         => ({type: types.DELETE_TODO, id});
export const updateTodo     = (id, description)            => ({type: types.UPDATE_TODO, id, description});
export const completeTodo   = (id, completed)              => ({type: types.COMPLETE_TODO, id, completed});
export const clearCompleted = ()                           => ({type: types.CLEAR_COMPLETED});

export function fetchAllTodos() {
  return (dispatch) => {
    request
      .get('/api/tasks')
      .end(function (err, res) {
        console.log(err, res);
        if (!err) {
          dispatch(initTodos(res.body));
        }
      });
  }
}

export function addTodoRequest(text) {
  return (dispatch) => {
    request
      .post('/api/tasks')
      .send({description: text})
      .end(function (err, res) {
        console.log(err, res);
      });
  }
}

export function updateTodoRequest(id, text) {
  return (dispatch) => {
    request
      .put('/api/tasks/' + id)
      .send({description: text})
      .end(function (err, res) {
        console.log(err, res);
      });
  }
}

export function toggleTodoRequest(id, completed) {
  return (dispatch) => {
    request
      .put('/api/tasks/' + id)
      .send({completed: completed})
      .end(function (err, res) {
        console.log(err, res);
      });
  }
}

export function deleteTodoRequest(id) {
  return (dispatch) => {
    request
      .delete('/api/tasks/' + id)
      .end(function (err, res) {
        console.log(err, res);
      });
  }
}

export function clearCompletedRequest() {
  return (dispatch) => {
    request
      .delete('/api/tasks')
      .end(function (err, res) {
        console.log(err, res);
      });
  }
}

