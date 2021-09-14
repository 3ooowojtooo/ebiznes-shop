import React from "react";
import {Link} from "react-router-dom";

const AddressItem = ({item, deleteHandle}) => {
    return (
        <tr>
            <td>{item.id}</td>
            <td>{item.street}</td>
            <td>{item.city}</td>
            <td>{item.zipcode}</td>
            <td><Link to={{
                pathname: "/addressEdit",
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

export default AddressItem;