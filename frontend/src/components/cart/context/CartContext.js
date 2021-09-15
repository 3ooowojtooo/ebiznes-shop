import React, {useEffect, useState} from "react";
import {addProductToUserCart, buyUserCart, deleteItemFromUserCart, getUserCart} from "../../../service/api/Api";

export const CartContext = React.createContext();
export const useCart = () => React.useContext(CartContext);

export const CartContextProvider = ({children}) => {
    const [cart, setCart] = useState()

    const reloadCartData = async () => {
        await getUserCart()
            .then(response => setCart(response.data))
    }

    useEffect(() => {
        reloadCartData().then()
    }, [])

    const getCartSize = () => {
        if (cart === undefined) {
            return 0
        } else {
            let count = 0
            for (let item of cart.items) {
                count += item.amount
            }
            return count
        }
    }

    const getCartPrice = () => {
        if (cart === undefined) {
            return 0
        } else {
            let price = 0.0
            for (let item of cart.items) {
                price += item.amount * item.product.price
            }
            return price
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

    const buyCart = async (paymentMethodId, userAddressId) => {
        await buyUserCart(paymentMethodId, userAddressId)
            .catch(err => alert(err))
        await reloadCartData()
    }

    return (
        <CartContext.Provider value={{
            getCartSize,
            getCartPrice,
            getCart,
            addProductToCart,
            deleteItemFromCart,
            buyCart
        }}>
            {children}
        </CartContext.Provider>
    )
}