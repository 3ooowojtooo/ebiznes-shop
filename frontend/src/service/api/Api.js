import axios from "axios";
import {BACKEND_BASE_URL} from "../../const";
import Cookies from 'js-cookie';

axios.defaults.withCredentials = true

export const signOut = () => {
    const url = BACKEND_BASE_URL + "/signOut"
    return axios.post(url, {}, {
        headers : {
            "Csrf-Token": Cookies.get("csrfToken")
        }
    })
       // .catch(_ => window.location.reload(false))
}