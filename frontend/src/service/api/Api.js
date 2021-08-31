import axios from "axios";
import {ENDPOINT} from "../../const";

axios.defaults.withCredentials = true

export const signOut = () => {
    const url = ENDPOINT + "/signOut"
    return axios.get(url)
       // .catch(_ => window.location.reload(false))
}