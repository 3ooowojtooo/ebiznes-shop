import React, {useState} from "react";
import {signUp} from "../../service/api/Api";

function SignUp() {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')

    const handleEmailChange = event => {
        setEmail(event.target.value)
    }

    const handlePasswordChange = event => {
        setPassword(event.target.value)
    }

    const handleSubmit = async event => {
        event.preventDefault()
        alert('Email: ' + email + ", password: " + password)
        await signUp(email, password)
            .then(_ => alert("Success"))
            .catch(err => alert(err))
    }

    return (
        <center>
            <form onSubmit={handleSubmit}>
                <label>
                    Email:
                    <input type="email" onChange={handleEmailChange} value={email} />
                </label>
                <label>
                    Password:
                    <input type="password" onChange={handlePasswordChange} value={password} />
                </label>
                <input type="submit" value="Sign up"/>
            </form>
        </center>
    )
}

export default SignUp;