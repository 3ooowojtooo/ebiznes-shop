import React, { useEffect, useState } from "react";
import Cookies from 'js-cookie';
import {ENDPOINT} from "../../../const";

export const AuthContext = React.createContext();
export const useAuth = () => React.useContext(AuthContext);

export const AuthContextProvider = ({children}) => {
  const [token, setToken] = useState("");

  const redirectToGoogleLogin = () => {
    window.location.href = ENDPOINT + "/authenticate/google";
  };

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
      redirectToGoogleLogin
    }}>
      {children}
    </AuthContext.Provider>
  );
};