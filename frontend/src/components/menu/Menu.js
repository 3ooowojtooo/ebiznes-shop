import React from "react";
import {useAuth} from "../auth/context/AuthContext";
import {Link} from "react-router-dom";

function Menu() {
    const {isLogged} = useAuth()
    return (
        isLogged() ?
            <center>
                <Link to="/">Index</Link><br/>
                <Link to="/user">Account details</Link><br/>
                <Link to="/history">Purchase history</Link><br/>
                <Link to="/paymentMethods">Payment methods</Link><br/>
                <Link to="/addresses">Addresses</Link><br/>
            </center>
            :
            <center>
                <Link to="/">Index</Link>
            </center>
    )
}

export default Menu;