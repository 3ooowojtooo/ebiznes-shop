import React from 'react';
import {useAuth} from "../context/AuthContext";
import {Link} from "react-router-dom";

function Auth() {
        const {isLogged,logOut} = useAuth()
        return (
            isLogged() ?
                <div>
                    <center>You are signed in. <button onClick={logOut}>Sign out</button></center>
                </div> :
                <div>
                    <center>You are not signed in. <Link to="/signIn">Sign in</Link> <Link to="/signUp">Sign up</Link></center>
                </div>
        );
}

export default Auth;