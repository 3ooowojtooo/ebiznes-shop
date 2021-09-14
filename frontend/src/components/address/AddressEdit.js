import React, {useState} from "react";
import {useLocation} from "react-router";
import {updateUserAddress} from "../../service/api/Api";

function AddressEdit() {
    const location = useLocation()
    const {item} = location.state
    const initialStreet = item.street
    const initialCity = item.city
    const initialZipCode = item.zipcode
    const [street, setStreet] = useState(initialStreet)
    const [city, setCity] = useState(initialCity)
    const [zipCode, setZipCode] = useState(initialZipCode)

    const onSubmit = event => {
        event.preventDefault()
        if (street === undefined || street === '' || city === undefined || city === '' || zipCode === undefined || zipCode === '') {
            alert("Fill in all address data")
        } else if (street === initialStreet && city === initialCity && zipCode === initialZipCode) {
            alert("Change at least one part of the address")
        } else {
            updateUserAddress(item.id, street, city, zipCode)
                .then(_ => window.location.href = "/addresses")
                .catch(err => alert(err.response.data))
        }
    }

    return (
        <center>
            <p>Edit address with id {item.id}</p>
            <form onSubmit={onSubmit}>
                <label>Enter new street: </label>
                <input type="text" value={street} onChange={event => setStreet(event.target.value)}/><br/>
                <label>Enter new city: </label>
                <input type="text" value={city} onChange={event => setCity(event.target.value)}/><br/>
                <label>Enter new zip code: </label>
                <input type="text" value={zipCode} onChange={event => setZipCode(event.target.value)}/><br/>
                <input type="submit" value="Change address"/>
            </form>
        </center>
    )
}

export default AddressEdit;