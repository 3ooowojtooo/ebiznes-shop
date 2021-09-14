import React from "react";
import PurchaseHistoryEntryCartItem from "./PurchaseHistoryEntryCartItem";

const PurchaseHistoryEntry = ({entry}) => {
    let itemViews = entry.cartItems.map(item => <PurchaseHistoryEntryCartItem key={item.id} item={item}/>)
    return (
        <div>
            <b>Total price: </b> {entry.purchaseHistory.totalPrice}<br/>
            <b>Purchase time: </b> {entry.purchaseHistory.purchaseTimestamp}<br/>
            <table>
                <thead>
                <tr>
                    <th>Product</th>
                    <th>Category</th>
                    <th>Amount</th>
                    <th>Price</th>
                </tr>
                </thead>
                <tbody>
                {itemViews}
                </tbody>
            </table>
            <br/><br/>
        </div>
    )
}

export default PurchaseHistoryEntry;