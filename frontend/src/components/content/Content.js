import React from "react";
import {Route, Switch} from "react-router";
import SignIn from "../auth/signIn/SignIn";
import SignUp from "../auth/signUp/SignUp";
import "./Content.css"
import Main from "../main/Main";
import PrivateRoute from "../auth/privateRoute/PrivateRoute";
import UserDetails from "../user/UserDetails";
import PublicRoute from "../auth/publicRoute/PublicRoute";
import Cart from "../cart/Cart";
import CartPurchase from "../cart/CartPurchase";
import PurchaseHistory from "../history/PurchaseHistory";
import PaymentMethods from "../paymentmethod/PaymentMethods";
import PaymentMethodEdit from "../paymentmethod/PaymentMethodEdit";
import Addresses from "../address/Addresses";
import AddressEdit from "../address/AddressEdit";

function Content() {
    return (
        <div className="Content">
            <Switch>
                <PublicRoute path="/signIn">
                    <SignIn/>
                </PublicRoute>
                <PublicRoute path="/signUp">
                    <SignUp/>
                </PublicRoute>
                <PrivateRoute path="/user">
                    <UserDetails/>
                </PrivateRoute>
                <PrivateRoute path="/cart">
                    <Cart/>
                </PrivateRoute>
                <PrivateRoute path="/cartPurchase">
                    <CartPurchase/>
                </PrivateRoute>
                <PrivateRoute path="/history">
                    <PurchaseHistory/>
                </PrivateRoute>
                <PrivateRoute path="/paymentMethods">
                    <PaymentMethods/>
                </PrivateRoute>
                <PrivateRoute path="/paymentMethodEdit">
                    <PaymentMethodEdit/>
                </PrivateRoute>
                <PrivateRoute path="/addresses">
                    <Addresses/>
                </PrivateRoute>
                <PrivateRoute path="/addressEdit">
                    <AddressEdit/>
                </PrivateRoute>
                <Route path="/">
                    <Main />
                </Route>
            </Switch>
        </div>
    )
}

export default Content;