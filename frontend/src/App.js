import React, {Component} from 'react';
import Auth from "./components/auth/Auth";
import Title from "./components/title/Title";
import {AuthContextProvider} from "./components/auth/context/AuthContext";
import Content from "./components/content/Content";
import {BrowserRouter} from "react-router-dom";

class App extends Component {
    render() {
        return (
            <BrowserRouter>
                <AuthContextProvider>
                    <Title/>
                    <Auth/>
                    <Content/>
                </AuthContextProvider>
            </BrowserRouter>
        );
    }
}

export default App;
