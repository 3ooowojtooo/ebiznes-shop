import React from "react";
import {useAuth} from "./context/AuthContext";
import {Redirect, Route} from "react-router";

function PrivateRoute({children, ...rest}) {
    const {isLogged} = useAuth();
    return (
        <Route
            {...rest}
            render={({ location }) =>
                isLogged() ? (
                    children
                ) : (
                    <Redirect
                        to={{
                            pathname: "/signIn",
                            state: { from: location }
                        }}
                    />
                )
            }
        />
    )
}

export default PrivateRoute;