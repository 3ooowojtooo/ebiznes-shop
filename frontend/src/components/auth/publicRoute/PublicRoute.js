import React from "react";
import {useAuth} from "../context/AuthContext";
import {Redirect, Route} from "react-router";

function PublicRoute({children, ...rest}) {
    const {isLogged} = useAuth();
    return (
        <Route
            {...rest}
            render={({location}) =>
                isLogged() ? (
                    <Redirect
                        to={{
                            pathname: "/",
                            state: {from: location}
                        }}
                    />
                ) : (
                    children
                )
            }
        />
    )
}

export default PublicRoute;