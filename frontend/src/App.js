import React, { Component } from 'react';
import './App.css';
import Auth from "./components/auth/Auth";
import {AuthContextProvider} from "./components/auth/context/AuthContext";

class App extends Component {
  render() {
    return (
        <AuthContextProvider>
          <Auth/>
        </AuthContextProvider>
    );
  }
}

export default App;
