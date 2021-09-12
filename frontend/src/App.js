import React, {Component} from 'react';
import Auth from "./components/auth/auth/Auth";
import Title from "./components/title/Title";
import {AuthContextProvider} from "./components/auth/context/AuthContext";
import Content from "./components/content/Content";
import {BrowserRouter} from "react-router-dom";
import Menu from "./components/menu/Menu";
import {CartContextProvider} from "./components/cart/context/CartContext";
import CartBar from "./components/cart/CartBar";

class App extends Component {
    render() {
        return (
            <BrowserRouter>
                <AuthContextProvider>
                    <CartContextProvider>
                        <Title/>
                        <Auth/>
                        <CartBar/>
                        <Menu/>
                        <Content/>
                    </CartContextProvider>
                </AuthContextProvider>
            </BrowserRouter>
        );
    }
}

export default App;
