import React from "react";
import {useCart} from "./context/CartContext";
import CartItem from "./CartItem";

function Cart() {
    const {getCart, getCartSize, getCartPrice} = useCart();
    let cart = getCart()
    let items = cart.items.map(item => <CartItem key={item.id} item={item}/>)
    return (
        cart.items.length === 0 ?
            <center>Your cart is empty</center> :
            <center>
                <table>
                    <thead>
                    <tr>
                        <th>Product</th>
                        <th>Category</th>
                        <th>Amount</th>
                        <th>Price</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    {items}
                    </tbody>
                </table><br/>
                <b>Total amount: </b> {getCartSize()}<br/>
                <b>Total price: </b> {getCartPrice()}<br/>
                <button>Purchase</button>
            </center>
    )
}

export default Cart;