import React from "react";

const StockItem = ({item}) => {
    return (
        <tr>
            <td>{item.product.name}</td>
            <td>{item.product.description}</td>
            <td>{item.product.category.name}</td>
            <td>{item.amount}</td>
            <td>{item.product.price}</td>
            <td>{item.amount > 0 ? <button>Add to cart</button> : "Unavailable"}</td>
        </tr>
    )
}

export default StockItem;