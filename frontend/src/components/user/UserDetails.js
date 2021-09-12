import React, {useEffect, useState} from "react";
import {getUserDetails} from "../../service/api/Api";

function UserDetails() {
    const [user, setUser] = useState();
    useEffect(() => {
        getUserDetails()
            .then(response => setUser(response.data))
            .catch(err => alert(err))
    }, [])

    return (
        user === undefined ? <center>Loading...</center> :
            <center>
                <b>Id</b>: {user.id}<br/>
                <b>E-mail</b>: {user.email}<br/>
                <b>Provider id</b>: {user.providerId}<br/>
                <b>Provider key</b>: {user.providerKey}
            </center>
    )
}

export default UserDetails;