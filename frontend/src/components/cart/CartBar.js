import React from "react";
import {useAuth} from "../auth/context/AuthContext";
import {useCart} from "./context/CartContext";

function CartBar() {
    const {isLogged} = useAuth();
    const {getCartSize} = useCart();

    return (
        isLogged() ? <center>You have {getCartSize()} items in your cart</center> : null
    )
}

export default CartBar;