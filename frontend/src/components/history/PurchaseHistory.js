import React, {useEffect} from "react";
import {getUserPurchaseHistory} from "../../service/api/Api";

function PurchaseHistory() {
    useEffect(async () => {
        await getUserPurchaseHistory()
    })

    return (
        <center><p>Purchase history</p></center>
    )
}

export default PurchaseHistory;