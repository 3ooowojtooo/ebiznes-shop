import React from "react";
import {Route, Switch} from "react-router";
import SignIn from "../auth/SignIn";
import SignUp from "../auth/SignUp";
import "./Content.css"

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
            </Switch>
        </div>
    )
}

export default Content;