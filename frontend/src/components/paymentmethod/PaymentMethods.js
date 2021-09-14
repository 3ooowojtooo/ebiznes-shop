import React, {useEffect, useState} from "react";
import {createUserPaymentMethod, deleteUserPaymentMethod, getUserPaymentMethods} from "../../service/api/Api";
import PaymentMethodItem from "./PaymentMethodItem";

function PaymentMethods() {
    const [methods, setMethods] = useState()
    const [newMethod, setNewMethod] = useState('')

    const reloadMethods = async () => {
        await getUserPaymentMethods()
            .then(response => setMethods(response.data))
    }

    const deleteHandle = async itemId => {
        await deleteUserPaymentMethod(itemId)
        await reloadMethods()
    }

    const addOnChange = event => {
        setNewMethod(event.target.value)
    }

    const addHandle = event => {
        event.preventDefault()
        if (newMethod !== undefined && newMethod !== '') {
            createUserPaymentMethod(newMethod)
                .then(_ => reloadMethods())
                .catch(err => alert(err.response.data))
        } else {
            alert("Cannot create payment method with empty name")
        }
    }

    useEffect(async () => {
        await reloadMethods()
    }, [])

    if (methods === undefined) {
        return (<center><p>Loading...</p></center>)
    }

    let listView

    if (methods.length === 0) {
        listView = <center><p>You have no payment methods</p></center>
    } else {
        let itemViews = methods.map(method => <PaymentMethodItem key={method.id} item={method} deleteHandle={deleteHandle}/>)
        listView =
            <center>
                <table>
                    <thead>
                    <tr>
                        <th>Id</th>
                        <th>Name</th>
                        <th>Edit</th>
                        <th>Delete</th>
                    </tr>
                    </thead>
                    <tbody>
                    {itemViews}
                    </tbody>
                </table>
            </center>
    }

    let addView =
        <center>
            <p>Add new method:</p><br/>
            <form onSubmit={addHandle}>
                <label>Method name: </label>
                <input type="text" value={newMethod} onChange={addOnChange} /><br/>
                <input type="submit" value="Add method"/>
            </form>
        </center>

    return (
        <div>
            {listView}<br/><br/>
            {addView}
        </div>
    )
}

export default PaymentMethods;