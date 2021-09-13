import React from "react";
import {useAuth} from "../auth/context/AuthContext";
import {useCart} from "./context/CartContext";
import {Link} from "react-router-dom";

function CartBar() {
    const {isLogged} = useAuth();
    const {getCartSize} = useCart();

    return (
        isLogged() ? <center>You have {getCartSize()} items in your <Link to="/cart">cart</Link></center> : null
    )
}

export default CartBar;