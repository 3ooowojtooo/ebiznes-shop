import React, {useState} from "react";

export const CartContext = React.createContext();
export const useCart = () => React.useContext(CartContext);

export const CartContextProvider = ({children}) => {
    const [items, setItems] = useState([])

    const getCartSize = () => {
        return items.length
    }

    return (
        <CartContext.Provider value={{
            getCartSize
        }}>
            {children}
        </CartContext.Provider>
    )
}