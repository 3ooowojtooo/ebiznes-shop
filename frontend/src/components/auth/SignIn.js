import React from "react";
import {useAuth} from "./context/AuthContext";

function SignIn() {
    const {redirectToGoogleLogin} = useAuth();
    return (
        <center><button onClick={redirectToGoogleLogin}>Sign in with Google</button></center>
    )
}

export default SignIn;