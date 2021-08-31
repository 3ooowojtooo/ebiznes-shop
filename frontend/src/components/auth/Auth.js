import React from 'react';
import {useAuth} from "./context/AuthContext";
import {signOut} from "../../service/api/Api";

function Auth() {
        const {redirectToGoogleLogin,isLogged,getToken} = useAuth()
        return (
            isLogged() ?
                <div>
                    <center><p>Jesteś zalogowany. Token to: {getToken()}</p></center><br/>
                    <center><button onClick={signOut}>Wyloguj</button></center>
                </div> :
                <div>
                    <center><button onClick={redirectToGoogleLogin}>Zaloguj się przez Google</button></center>
                </div>
        );
}

export default Auth;