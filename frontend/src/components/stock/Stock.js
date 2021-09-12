import React, {useEffect, useState} from "react";
import {getStockItems} from "../../service/api/Api";
import StockItem from "./StockItem";

function Stock() {
    const [items, setItems] = useState();
    useEffect(() => {
        getStockItems()
            .then(response => {
                if (response.status === 200) {
                    let parsedItems = response.data.map(item => <StockItem key={item.id} item={item}/>)
                    setItems(parsedItems)
                } else {
                    alert("not ok")
                    alert(response)
                }
            })
            .catch(err => alert(err))
    }, [])
    return (
        items === undefined ? <center>"loading"</center> :
            <center>
                <table>
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Description</th>
                            <th>Category</th>
                            <th>In stock</th>
                            <th>Price</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        {items}
                    </tbody>
                </table>
            </center>
    )
}

export default Stock;