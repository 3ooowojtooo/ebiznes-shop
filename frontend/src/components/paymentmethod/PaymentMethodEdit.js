import React, {useState} from "react";
import {useLocation} from "react-router";
import {updateUserPaymentMethod} from "../../service/api/Api";

function PaymentMethodEdit() {
    const location = useLocation()
    const {item} = location.state
    const initialName = item.name
    const [name, setName] = useState(initialName)

    const onSubmit = event => {
        event.preventDefault()
        if (name === undefined || name === '') {
            alert("Payment method name cannot be empty")
        } else if (name === initialName) {
            alert("Change payment name")
        } else {
            updateUserPaymentMethod(item.id, name)
                .then(_ => window.location.href = "/paymentMethods")
                .catch(err => alert(err.response.data))
        }
    }

    return (
        <center>
            <p>Edit payment method with id {item.id}</p>
            <form onSubmit={onSubmit}>
                <label>Enter new payment name: </label>
                <input type="text" value={name} onChange={event => setName(event.target.value)}/><br/>
                <input type="submit" value="Change payment method"/>
            </form>
        </center>
    )
}

export default PaymentMethodEdit;