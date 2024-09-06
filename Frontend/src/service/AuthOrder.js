import axios from "axios";
import authHeader from "./AuthHeader";

const API_URL = "https://carpentryshop-server-production.up.railway.app"

const addToCart = async (postData) => {
    const cart = JSON.parse(localStorage.getItem("cart")) || [];

    if (cart.length === 0) {
        createNewCart(postData);
    } else {
        var duplicate = findDuplicate(cart, postData);
        if (!duplicate) {
            addNewItem(cart, postData)
        }
    }
};

const createNewCart = (postData) => {
    const newCart = [];
    newCart.push(postData)
    localStorage.setItem("cart", JSON.stringify(newCart))
}

const findDuplicate = (cart, postData) => {
    var duplicate = false;
    cart.forEach(element => {
        if (element.project.id === postData.project.id) {
            element.quantity += postData.quantity;
            localStorage.setItem("cart", JSON.stringify(cart))
            duplicate = true;
        }
    });
    return duplicate;
}

const addNewItem = (cart, postData) => {
    const newItem = [...cart, postData]
    localStorage.setItem("cart", JSON.stringify(newItem))
}


const decrease = async (id) => {
    const cart = JSON.parse(localStorage.getItem("cart"));
    cart.find((element) => {
        if (element.project.id === id && element.quantity > 1) {
            element.quantity -= 1
        }
    })
    localStorage.setItem("cart", JSON.stringify(cart))
    window.location.reload()

}

const increase = async (id) => {
    const cart = JSON.parse(localStorage.getItem("cart"));
    cart.find((element) => {
        if (element.project.id === id) {
            element.quantity += 1
        }
    })
    localStorage.setItem("cart", JSON.stringify(cart))
    window.location.reload()

}

const deleteItem = async (id) => {
    const cart = JSON.parse(localStorage.getItem("cart"));
    const newItems = cart.filter(item => item.project.id !== id)
    console.log(newItems)
    localStorage.setItem("cart", JSON.stringify(newItems))
}

const totalPrice = () => {
    const cart = JSON.parse(localStorage.getItem("cart"));
    var totalPrice = 0;
    cart && cart.forEach(element => {
        totalPrice += element.project.price * element.quantity
    });
    return totalPrice;
}

const getCurrentOrder = () => {
    return JSON.parse(localStorage.getItem("cart"));
}


const getOrderById = async (orderId) => {
    return await axios.get(API_URL + `/api/order/getOrderById?orderId=${orderId}`, { headers: authHeader() });
}

const acceptOrder = async (isPaid) => {
    const cart = JSON.parse(localStorage.getItem("cart"));
    return await axios.post(API_URL + `/api/order/add?isPaid=${isPaid}`, cart, { headers: authHeader() });
}

const changePaidStatus = async (id) => {
    return await axios.put(API_URL + `/api/order/changePaidStatus?orderId=${id}`, null, { headers: authHeader() });
}

const createPayment = async () => {
    const cart = JSON.parse(localStorage.getItem("cart"));
    return await axios.post(API_URL + '/api/order/payment', cart, { headers: authHeader() });
}

const createPaymentToLater = async (jobToPay) => {
    localStorage.setItem("cartToPayLater", JSON.stringify(jobToPay))
    return await axios.post(API_URL + '/api/order/payment', jobToPay.projects, { headers: authHeader() });
}




const AuthOrder = {
    addToCart,
    decrease,
    increase,
    deleteItem,
    totalPrice,
    getCurrentOrder,
    getOrderById,
    acceptOrder,
    createPayment,
    createPaymentToLater,
    changePaidStatus
};

export default AuthOrder;