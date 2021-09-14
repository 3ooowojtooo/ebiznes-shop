import React, {useEffect, useState} from "react";
import {createUserAddress, deleteUserAddress, getUserAddresses} from "../../service/api/Api";
import AddressItem from "./AddressItem";

function Addresses() {
    const [addresses, setAddresses] = useState()
    const [newStreet, setNewStreet] = useState('')
    const [newCity, setNewCity] = useState('')
    const [newZipCode, setNewZipCode] = useState('')

    const reloadAddresses = async () => {
        await getUserAddresses()
            .then(response => setAddresses(response.data))
    }

    const deleteHandle = async itemId => {
        await deleteUserAddress(itemId)
        await reloadAddresses()
    }

    const newStreetOnChange = event => {
        setNewStreet(event.target.value)
    }

    const newCityOnChange = event => {
        setNewCity(event.target.value)
    }

    const newZipCodeOnChange = event => {
        setNewZipCode(event.target.value)
    }

    const addHandle = event => {
        event.preventDefault()
        if (newStreet !== undefined && newStreet !== '' && newCity !== undefined && newCity !== '' && newZipCode !== undefined && newZipCode !== '') {
            createUserAddress(newStreet, newCity, newZipCode)
                .then(_ => reloadAddresses())
                .catch(err => alert(err.response.data))
        } else {
            alert("Fill in all address data")
        }
    }

    useEffect(async () => {
        await reloadAddresses()
    }, [])

    if (addresses === undefined) {
        return (<center><p>Loading...</p></center>)
    }

    let listView

    if (addresses.length === 0) {
        listView = <center><p>You have no addresses</p></center>
    } else {
        let itemViews = addresses.map(address => <AddressItem key={address.id} item={address}
                                                              deleteHandle={deleteHandle}/>)
        listView =
            <center>
                <table>
                    <thead>
                    <tr>
                        <th>Id</th>
                        <th>Street</th>
                        <th>City</th>
                        <th>Zip code</th>
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
            <p>Add new address:</p><br/>
            <form onSubmit={addHandle}>
                <label>Street: </label>
                <input type="text" value={newStreet} onChange={newStreetOnChange}/><br/>
                <label>City: </label>
                <input type="text" value={newCity} onChange={newCityOnChange}/><br/>
                <label>Zip code: </label>
                <input type="text" value={newZipCode} onChange={newZipCodeOnChange}/><br/>
                <input type="submit" value="Add address"/>
            </form>
        </center>

    return (
        <div>
            {listView}<br/><br/>
            {addView}
        </div>
    )
}

export default Addresses;