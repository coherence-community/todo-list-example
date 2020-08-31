/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

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
