import React from "react";
import {Route, Switch} from "react-router";
import SignIn from "../auth/signIn/SignIn";
import SignUp from "../auth/signUp/SignUp";
import "./Content.css"
import Main from "../main/Main";
import PrivateRoute from "../auth/privateRoute/PrivateRoute";
import UserDetails from "../user/UserDetails";
import PublicRoute from "../auth/publicRoute/PublicRoute";
import Cart from "../cart/Cart";

function Content() {
    return (
        <div className="Content">
            <Switch>
                <PublicRoute path="/signIn">
                    <SignIn/>
                </PublicRoute>
                <PublicRoute path="/signUp">
                    <SignUp/>
                </PublicRoute>
                <PrivateRoute path="/user">
                    <UserDetails/>
                </PrivateRoute>
                <PrivateRoute path="/cart">
                    <Cart/>
                </PrivateRoute>
                <Route path="/">
                    <Main />
                </Route>
            </Switch>
        </div>
    )
}

export default Content;