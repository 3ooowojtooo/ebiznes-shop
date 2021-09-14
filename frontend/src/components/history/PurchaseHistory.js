import React, {useEffect, useState} from "react";
import {getUserPurchaseHistory} from "../../service/api/Api";
import PurchaseHistoryEntry from "./PurchaseHistoryEntry";

function PurchaseHistory() {
    const [entries, setEntries] = useState()

    const reloadEntries = async () => {
        await getUserPurchaseHistory()
            .then(response => setEntries(response.data))
    }

    useEffect(async() => {
        await reloadEntries()
    }, [])

    if (entries === undefined) {
        return (<center><p>Loading...</p></center>)
    }

    if (entries.length === 0) {
        return (<center><p>You have never purchased anything in our shop</p></center>)
    }

    let entryViews = entries.map(entry => <PurchaseHistoryEntry key={entry.purchaseHistory.id} entry={entry}/>)

    return (<center>{entryViews}</center>)
}

export default PurchaseHistory;