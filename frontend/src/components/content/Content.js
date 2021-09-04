import React from "react";
import {Route, Switch} from "react-router";
import SignIn from "../auth/SignIn";
import SignUp from "../auth/SignUp";

function Content() {
    return (
    <Switch>
        <Route path="/signIn">
            <SignIn/>
        </Route>
        <Route path="/signUp">
            <SignUp/>
        </Route>
    </Switch>
    )
}

export default Content;