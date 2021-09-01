import React from 'react';
import {useAuth} from "./context/AuthContext";

function Auth() {
        const {redirectToGoogleLogin,isLogged,getToken,logOut} = useAuth()
        return (
            isLogged() ?
                <div>
                    <center><p>Jesteś zalogowany. Token to: {getToken()}</p></center><br/>
                    <center><button onClick={logOut}>Wyloguj</button></center>
                </div> :
                <div>
                    <center><button onClick={redirectToGoogleLogin}>Zaloguj się przez Google</button></center>
                </div>
        );
}

export default Auth;