/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

import React, { Component } from 'react'
import PropTypes from 'prop-types'
import TodoItem from './TodoItem'
import Footer from './Footer'
import { SHOW_ALL, SHOW_COMPLETED, SHOW_ACTIVE } from '../constants/TodoFilters'

const TODO_FILTERS = {
  [SHOW_ALL]:       () => true,
  [SHOW_ACTIVE]:    todo => !todo.completed,
  [SHOW_COMPLETED]: todo => todo.completed
};

class MainSection extends Component {
  static propTypes = {
    todos:   PropTypes.array.isRequired,
    actions: PropTypes.object.isRequired
  };

  state = {filter: SHOW_ALL};

  handleClearCompleted = () => {
    this.props.actions.clearCompletedRequest()
  };

  handleShow = filter => {
    this.setState({filter})
  };

  renderFooter(completedCount) {
    const { todos } = this.props;
    const { filter } = this.state;
    const activeCount = todos.length - completedCount;

    if (todos.length) {
      return (
        <Footer completedCount={completedCount}
                activeCount={activeCount}
                filter={filter}
                onClearCompleted={this.handleClearCompleted}
                onShow={this.handleShow}/>
      )
    }
  }

  render() {
    const { todos, actions } = this.props;
    const { filter } = this.state;

    const filteredTodos = todos.filter(TODO_FILTERS[filter]).sort(function (todoLhs, todoRhs) {
      return todoLhs.createdAt - todoRhs.createdAt
    });
    const completedCount = todos.reduce((count, todo) =>
        todo.completed ? count + 1 : count,
      0
    );

    return (
      <section className="main">
        <ul className="todo-list">
          {filteredTodos.map(todo =>
              <TodoItem key={todo.id} todo={todo} {...actions} />
          )}
        </ul>
        {this.renderFooter(completedCount)}
      </section>
    )
  }
}

export default MainSection;
