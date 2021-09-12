import React from "react";
import {useAuth} from "../auth/context/AuthContext";
import {Link} from "react-router-dom";

function Menu() {
    const {isLogged} = useAuth()
    return (
        isLogged() ?
            <center>
                <Link to="/">Index</Link><br/>
                <Link to="/user">Account details</Link>
            </center>
            :
            <center>
                <Link to="/">Index</Link>
            </center>
    )
}

export default Menu;