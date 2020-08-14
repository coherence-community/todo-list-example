/*
 Copyright (c) 2020, Oracle and/or its affiliates.

 Licensed under the Universal Permissive License v 1.0 as shown at
 http://oss.oracle.com/licenses/upl.
*/

import React, { Component } from 'react'
import PropTypes from 'prop-types'
import classnames from 'classnames'
import TodoTextInput from './TodoTextInput'

export default class TodoItem extends Component {
  static propTypes = {
    todo:              PropTypes.object.isRequired,
    updateTodoRequest: PropTypes.func.isRequired,
    deleteTodoRequest: PropTypes.func.isRequired,
    toggleTodoRequest: PropTypes.func.isRequired
  };

  state = {
    editing: false
  };

  handleDoubleClick = () => {
    this.setState({editing: true})
  };

  handleSave = (id, text) => {
    if (text.length === 0) {
      this.props.deleteTodoRequest(id)
    }
    else {
      this.props.updateTodoRequest(id, text)
    }
    this.setState({editing: false})
  };

  render() {
    const { todo, toggleTodoRequest, deleteTodoRequest } = this.props;

    let element;
    if (this.state.editing) {
      element = (
        <TodoTextInput text={todo.description}
                       editing={this.state.editing}
                       onSave={(text) => this.handleSave(todo.id, text)}/>
      )
    }
    else {
      element = (
        <div className="view">
          <input className="toggle"
                 type="checkbox"
                 checked={todo.completed}
                 onChange={() => toggleTodoRequest(todo.id, !todo.completed)}/>
          <label onDoubleClick={this.handleDoubleClick}>
            {todo.description}
          </label>
          <button className="destroy"
                  onClick={() => deleteTodoRequest(todo.id)}/>
        </div>
      )
    }

    return (
      <li className={classnames({
        completed: todo.completed,
        editing: this.state.editing
      })}>
        {element}
      </li>
    )
  }
}
