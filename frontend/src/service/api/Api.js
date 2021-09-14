import axios from "axios";
import {BACKEND_BASE_URL} from "../../const";
import Cookies from 'js-cookie';

const CSRF_TOKEN_COOKIE_NAME = "csrfToken"
const JSON_HEADERS = {
    "Content-Type": "application/json; charset=utf-8"
}

axios.defaults.withCredentials = true

export const signOut = () => {
    return postCall("/signOut")
}

export const signIn = (email, password) => {
    const body = {
        email : email,
        password : password
    }
    return postCall("/signIn", body, JSON_HEADERS)
}

export const signUp = (email, password) => {
    const body = {
        email : email,
        password : password
    }
    return postCall("/signUp", body, JSON_HEADERS)
}

export const getStockItems = () => {
    return getCall("/rest/stock")
}

export const getUserDetails = () => {
    return getCall("/rest/currentuser")
}

export const getUserCart = () => {
    return getCall("/rest/currentcart")
}

export const addProductToUserCart = productId => {
    return postCall("/rest/currentcart/product/" + productId)
}

export const deleteItemFromUserCart = itemId => {
    return deleteCall("/rest/currentcart/item/" + itemId)
}

export const getUserPaymentMethods = () => {
    return getCall("/rest/currentpaymentmethod")
}

export const updateUserPaymentMethod = (id, name) => {
    const body = {
        name : name
    }
    return putCall("/rest/currentpaymentmethod/" + id, body, JSON_HEADERS)
}

export const createUserPaymentMethod = name => {
    const body = {
        name : name
    }
    return postCall("/rest/currentpaymentmethod", body, JSON_HEADERS)
}

export const deleteUserPaymentMethod = id => {
    return deleteCall("/rest/currentpaymentmethod/" + id)
}

export const getUserAddresses = () => {
    return getCall("/rest/currentaddress")
}

export const updateUserAddress = (id, street, city, zipcode) => {
    const body = {
        street : street,
        city : city,
        zipcode : zipcode
    }
    return putCall("/rest/currentaddress/" + id, body, JSON_HEADERS)
}

export const createUserAddress = (street, city, zipcode) => {
    const body = {
        street : street,
        city : city,
        zipcode : zipcode
    }
    return postCall("/rest/currentaddress", body, JSON_HEADERS)
}

export const deleteUserAddress = id => {
    return deleteCall("/rest/currentaddress/" + id)
}

export const buyUserCart = (paymentMethodId, userAddressId) => {
    const body = {
        paymentMethod : paymentMethodId,
        userAddress : userAddressId
    }
    return postCall("/rest/currentcart", body, JSON_HEADERS)
}

export const getUserPurchaseHistory = () => {
    return getCall("/rest/currentpurchasehistory")
}

function getCall(url, headers = {}) {
    const fullUrl = buildUrl(url)
    const allHeaders = buildHeadersWithCsrfToken(headers)
    const config = buildConfigWithHeaders(allHeaders)
    return axios.get(fullUrl, config)
}

function postCall(url, data = {}, headers = {}) {
    const fullUrl = buildUrl(url)
    const allHeaders = buildHeadersWithCsrfToken(headers)
    const config = buildConfigWithHeaders(allHeaders)
    return axios.post(fullUrl, data, config)
}

function deleteCall(url, headers = {}) {
    const fullUrl = buildUrl(url)
    const allHeaders = buildHeadersWithCsrfToken(headers)
    const config = buildConfigWithHeaders(allHeaders)
    return axios.delete(fullUrl, config)
}

function putCall(url, data = {}, headers = {}) {
    const fullUrl = buildUrl(url)
    const allHeaders = buildHeadersWithCsrfToken(headers)
    const config = buildConfigWithHeaders(allHeaders)
    return axios.put(fullUrl, data, config)
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

function buildConfigWithHeaders(headers) {
    return {
        headers: headers
    }
}