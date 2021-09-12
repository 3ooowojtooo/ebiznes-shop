import React, { useEffect, useState } from "react";
import Cookies from 'js-cookie';
import {BACKEND_BASE_URL} from "../../../const";
import {signOut} from "../../../service/api/Api";

export const AuthContext = React.createContext();
export const useAuth = () => React.useContext(AuthContext);

export const AuthContextProvider = ({children}) => {
  const [token, setToken] = useState("");

  const redirectToGoogleLogin = () => {
    window.location.href = BACKEND_BASE_URL + "/authenticate/google";
  };

  const logOut = async () => {
      await signOut()
          .then(_ => window.location.href = "/")
  }

  function refreshLoggedInfo() {
    const tokenValue = Cookies.get("authenticator")
      setToken(tokenValue)
  }

  useEffect(() => {
      refreshLoggedInfo()
  })

  const isLogged = () => {
      return !!token;
  }

  const getToken = () => {
      return token
  }

  return (
    <AuthContext.Provider value={{
      isLogged,
      getToken,
      redirectToGoogleLogin,
        logOut
    }}>
      {children}
    </AuthContext.Provider>
  );
};