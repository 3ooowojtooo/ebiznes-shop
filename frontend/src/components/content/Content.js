import React from "react";
import {Route, Switch} from "react-router";
import SignIn from "../auth/signIn/SignIn";
import SignUp from "../auth/signUp/SignUp";
import "./Content.css"
import Main from "../main/Main";

function Content() {
    return (
        <div className="Content">
            <Switch>
                <Route path="/signIn">
                    <SignIn/>
                </Route>
                <Route path="/signUp">
                    <SignUp/>
                </Route>
                <Route path="/">
                    <Main />
                </Route>
            </Switch>
        </div>
    )
}

export default Content;