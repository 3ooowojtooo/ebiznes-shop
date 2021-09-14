import React from "react";
import {Link} from "react-router-dom";

const PaymentMethodItem = ({item, deleteHandle}) => {
    return (
        <tr>
            <td>{item.id}</td>
            <td>{item.name}</td>
            <td><Link to={{
                pathname: "/paymentMethodEdit",
                state: {
                    item: item
                }
            }}>Edit</Link></td>
            <td>
                <button onClick={_ => deleteHandle(item.id)}>Delete</button>
            </td>
        </tr>
    )
}

export default PaymentMethodItem;