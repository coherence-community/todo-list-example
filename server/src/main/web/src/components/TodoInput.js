import React, { PropTypes, Component } from 'react'
import TodoTextInput from './TodoTextInput'

class TodoInput extends Component {
  static propTypes = {
    addTodo: PropTypes.func.isRequired
  };

  handleSave = (text) => {
    if (text.length !== 0) {
      this.props.addTodo(text);
    }
  };

  render() {
    return (
      <TodoTextInput newTodo
                     onSave={this.handleSave}
                     placeholder="What needs to be done?" />
    )
  }
}

export default TodoInput;