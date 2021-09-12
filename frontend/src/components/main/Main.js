import React from "react";
import {useAuth} from "../auth/context/AuthContext";
import Stock from "../stock/Stock";

function Main() {
    const {isLogged} = useAuth()

    return (
        isLogged() ? <Stock /> : null
    )
}

export default Main;