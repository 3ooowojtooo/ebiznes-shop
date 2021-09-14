import React from "react";

const PurchaseHistoryEntryCartItem = ({item}) => {
    return (
        <tr>
            <td>{item.product.name}</td>
            <td>{item.product.category.name}</td>
            <td>{item.amount}</td>
            <td>{item.amount * item.product.price}</td>
        </tr>
    )
}

export default PurchaseHistoryEntryCartItem;