import React, { Component } from 'react'

class Header extends Component {
  render() {
    const title = "Tasks";
    const image = "todos.png";

    return (
      <header className="header">
        <h1><img src={image}/> {title}</h1>
      </header>
    )
  }
}

export default Header;
