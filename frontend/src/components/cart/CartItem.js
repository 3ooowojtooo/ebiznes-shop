import React from "react";
import {useCart} from "./context/CartContext";

const CartItem = ({item}) => {
    const {deleteItemFromCart} = useCart()

    return (
        <tr>
            <td>{item.product.name}</td>
            <td>{item.product.category.name}</td>
            <td>{item.amount}</td>
            <td>{item.amount * item.product.price}</td>
            <td><button onClick={_ => deleteItemFromCart(item.id)}>Delete from cart</button></td>
        </tr>
    )
}

export default CartItem;