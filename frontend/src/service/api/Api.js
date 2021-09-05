import axios from "axios";
import {BACKEND_BASE_URL} from "../../const";
import Cookies from 'js-cookie';

const CSRF_TOKEN_COOKIE_NAME = "csrfToken"

axios.defaults.withCredentials = true

export const signOut = () => {
    return post("/signOut")
}

function post(url, data = {}, headers = {}) {
    const fullUrl = buildUrl(url)
    const allHeaders = buildHeadersWithCsrfToken(headers)
    const config = {
        headers: allHeaders
    }
    return axios.post(fullUrl, data, config)
}

function buildUrl(url) {
    return BACKEND_BASE_URL + url
}

function buildHeadersWithCsrfToken(headers) {
    return Object.assign({}, headers, {
        "Csrf-Token": getCsrfToken()
    })
}

function getCsrfToken() {
    return Cookies.get(CSRF_TOKEN_COOKIE_NAME)
}