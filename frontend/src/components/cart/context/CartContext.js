import React, {useEffect, useState} from "react";
import {getUserCart} from "../../../service/api/Api";

export const CartContext = React.createContext();
export const useCart = () => React.useContext(CartContext);

export const CartContextProvider = ({children}) => {
    const [cart, setCart] = useState()

    useEffect(async () => {
        await getUserCart()
            .then(response => setCart(response.data))
    }, [])

    const getCartSize = () => {
        return cart === undefined ? 0 : cart.items.length
    }

    const getCart = () => {
        return cart
    }

    return (
        <CartContext.Provider value={{
            getCartSize,
            getCart
        }}>
            {children}
        </CartContext.Provider>
    )
}