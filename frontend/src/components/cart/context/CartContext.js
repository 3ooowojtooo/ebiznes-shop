import React, {useEffect, useState} from "react";
import {addProductToUserCart, deleteItemFromUserCart, getUserCart} from "../../../service/api/Api";

export const CartContext = React.createContext();
export const useCart = () => React.useContext(CartContext);

export const CartContextProvider = ({children}) => {
    const [cart, setCart] = useState()

    const reloadCartData = async () => {
        await getUserCart()
            .then(response => setCart(response.data))
    }

    useEffect(async () => await reloadCartData(), [])

    const getCartSize = () => {
        if (cart === undefined) {
            return 0
        } else {
            let count = 0
            for (let i = 0; i < cart.items.length; i++) {
                count += cart.items[i].amount
            }
            return count
        }
    }

    const getCart = () => {
        return cart
    }

    const addProductToCart = async productId => {
        await addProductToUserCart(productId)
        await reloadCartData()
    }

    const deleteItemFromCart = async itemId => {
        await deleteItemFromUserCart(itemId)
        await reloadCartData()
    }

    return (
        <CartContext.Provider value={{
            getCartSize,
            getCart,
            addProductToCart,
            deleteItemFromCart
        }}>
            {children}
        </CartContext.Provider>
    )
}