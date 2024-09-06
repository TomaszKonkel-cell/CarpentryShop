import React, { useEffect } from 'react';
import { Container } from 'react-bootstrap';

import 'bootstrap/dist/css/bootstrap.css';
import Swal from 'sweetalert2';
import AuthOrder from '../../service/AuthOrder';


const Success = () => {
    useEffect(() => {
        const payment = localStorage.getItem("payment");
        const cartToPayLater = JSON.parse(localStorage.getItem("cartToPayLater"));
        
        if (payment) {
            Swal.fire("Przyjmowanie zamówienia")
            Swal.showLoading();
            if (cartToPayLater) {
                AuthOrder.changePaidStatus(cartToPayLater.id).then(
                    (result) => {
                        Swal.close();
                        Swal.fire(result.data, "", "success");
                        localStorage.removeItem('cartToPayLater')
                        localStorage.removeItem('payment')
                    })
            } else {
                AuthOrder.acceptOrder(true).then(
                    (result) => {
                        Swal.close();
                        Swal.fire(result.data, "", "success");
                        localStorage.removeItem('cart')
                        localStorage.removeItem('payment')
                    })
            }

        } else {
            Swal.fire("Żadna płatność nie jest przetwarzana", "", "error")
        }

    }, [])

    return (

        <Container className="py-5 h-100">

        </Container >



    )
}

export default Success