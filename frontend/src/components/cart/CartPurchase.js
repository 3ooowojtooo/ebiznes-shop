import React, {useEffect, useState} from "react";
import {useCart} from "./context/CartContext";
import {getUserAddresses, getUserPaymentMethods} from "../../service/api/Api";
import {Redirect} from "react-router";

function CartPurchase() {
    const [addresses, setAddresses] = useState()
    const [paymentMethods, setPaymentMethods] = useState()
    const [selectedAddress, setSelectedAddress] = useState()
    const [selectedPaymentMethod, setSelectedPaymentMethod] = useState()
    const {getCart, getCartPrice, buyCart} = useCart()
    let cart = getCart()

    useEffect(async () => {
        await getUserAddresses().then(response => {
            let data = response.data
            setAddresses(data)
            if (data !== undefined && data.length > 0) {
                setSelectedAddress(data[0].id)
            }
        })
        await getUserPaymentMethods().then(response => {
            let data = response.data
            setPaymentMethods(data)
            if (data !== undefined && data.length > 0) {
                setSelectedPaymentMethod(data[0].id)
            }
        })
    }, [])

    if (cart === undefined || addresses === undefined || paymentMethods === undefined) {
        return (<center><p>Loading...</p></center>)
    }

    if (cart.items.length === 0) {
        return (<Redirect to="/cart"/>)
    }

    if (addresses.length === 0) {
        return <center><p>You have no addresses defined</p></center>
    }

    if (paymentMethods.length === 0) {
        return <center><p>You have no payment methods defined</p></center>
    }

    const handleAddressChange = event => {
        useEffect(() => setSelectedAddress(event.target.value), [])
    }

    const handlePaymentMethodChange = event => {
        setSelectedPaymentMethod(event.target.value)
    }

    const onCartBuy = async _ => {
        await buyCart(selectedPaymentMethod, selectedAddress)
        window.location.href = "/"
    }

    let addressesViews = addresses.map(val => <option key={val.id} value={val.id}>{val.street + ", " + val.zipcode + " " + val.city}</option>)
    let paymentMethodViews = paymentMethods.map(val => <option key={val.id} value={val.id}>{val.name}</option>)

    return (
        <center>
            <b>Total price: </b> {getCartPrice()}<br/>
            <form>
                <label>Pick address: </label>
                <select value={selectedAddress} onChange={handleAddressChange}>
                    {addressesViews}
                </select><br/>
                <label>Pick payment method: </label>
                <select value={selectedPaymentMethod} onChange={handlePaymentMethodChange}>
                    {paymentMethodViews}
                </select>
            </form><br/>
            <button onClick={onCartBuy}>Buy cart</button>
        </center>
    )
}

export default CartPurchase;