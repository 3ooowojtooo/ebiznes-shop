import React from "react";
import {useCart} from "../cart/context/CartContext";

const StockItem = ({item}) => {
    const {addProductToCart} = useCart();
    return (
        <tr>
            <td>{item.product.name}</td>
            <td>{item.product.description}</td>
            <td>{item.product.category.name}</td>
            <td>{item.amount}</td>
            <td>{item.product.price}</td>
            <td>{item.amount > 0 ? <button onClick={_ => addProductToCart(item.product.id)}>Add to cart</button> : "Unavailable"}</td>
        </tr>
    )
}

export default StockItem;