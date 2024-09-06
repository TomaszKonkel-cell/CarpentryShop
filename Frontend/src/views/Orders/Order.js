import React, { useState } from 'react';
import { Button, Card, Col, Container, Form, Modal, Row } from 'react-bootstrap';
import { BsDash, BsPlus, BsTrash } from "react-icons/bs";
import { useNavigate } from "react-router-dom";
import Swal from 'sweetalert2';

import AuthOrder from '../../service/AuthOrder';

import { CButton } from '@coreui/react';
import { CIcon } from '@coreui/icons-react';
import { cilCalendar, cilCash, cilCreditCard } from '@coreui/icons';

const Order = () => {
    const order = AuthOrder.getCurrentOrder();
    const totalPrice = AuthOrder.totalPrice();
    let navigate = useNavigate();

    const [show, setShow] = useState(false)

    const open = () => setShow(true);
    const close = () => setShow(false);

    const decrease = (id) => {
        AuthOrder.decrease(id)
    }

    const increase = (id) => {
        AuthOrder.increase(id)
    }

    const deleteItem = (id) => {
        Swal.fire({
            title: "Potwierdzasz usunięcie pozycji?",
            showDenyButton: true,
            confirmButtonText: "TAK",
            denyButtonText: `NIE`,
            icon: "question"
        }).then((result) => {

            if (result.isConfirmed) {
                AuthOrder.deleteItem(id).then(
                    () => {
                        Swal.fire("Pozycja usunięta", "", "success");
                        AuthOrder.deleteItem(id)
                        navigate('/Order')
                    })
            } else if (result.isDenied) {
                Swal.fire("Operacje anulowano", "", "info");
            }
        })
    }


    const payWithCash = () => {
        Swal.fire({
            title: "Wybrałeś metode płatności gotówką, czy jesteś pewny płatności?",
            showDenyButton: true,
            confirmButtonText: "TAK",
            denyButtonText: `NIE`,
            icon: "question"
        }).then((result) => {
            Swal.fire("Przyjmowanie zamówienia")
            Swal.showLoading();
            if (result.isConfirmed) {
                AuthOrder.acceptOrder(true).then(
                    (result) => {
                        Swal.close();
                        Swal.fire(result.data, "", "success");
                        localStorage.removeItem('cart')
                        navigate('/JobList')
                    }, (error) => {
                        Swal.close();
                        Swal.fire(error.response.data, "", "error");
                    })
            } else if (result.isDenied) {
                Swal.fire("Operacje anulowano", "", "info");
            }
        })
    }

    const payLater = () => {
        Swal.fire({
            title: "Wybrałeś metode płatności później, czy jestes pewien?",
            showDenyButton: true,
            confirmButtonText: "TAK",
            denyButtonText: `NIE`,
            icon: "question"
        }).then((result) => {
            Swal.fire("Przyjmowanie zamówienia")
            Swal.showLoading();
            if (result.isConfirmed) {
                AuthOrder.acceptOrder(false).then(
                    (result) => {
                        Swal.close();
                        Swal.fire(result.data, "", "success");
                        localStorage.removeItem('cart')
                        navigate('/JobList')
                    }, (error) => {
                        Swal.close();
                        Swal.fire(error.response.data, "", "error");
                    })
            } else if (result.isDenied) {
                Swal.fire("Operacje anulowano", "", "info");
            }
        })
    }

    const payWithPaymentGateway = () => {
        Swal.fire("Tworzenie płatności")
        Swal.showLoading();
        AuthOrder.createPayment().then(
            (result) => {
                Swal.close();
                if (result.data === "Error") {
                    Swal.fire("Dane przekazane do płatności są błędne sprawdź je", "", "error");
                } else {
                    const payment = true;
                    localStorage.setItem("payment", payment)
                    window.location.href = result.data
                }

            })
    }

    return (
        <Container className="py-5 h-100">
            <Row className="justify-content-center align-items-center h-100">
                <Col md="10">
                    <div className="d-flex justify-content-between align-items-center mb-4">
                        <h1 tag="h3" className="fw-normal mb-0 text-black">
                            Lista zamówienia
                        </h1>
                    </div>


                    {order && order.map((item) => {
                        return (
                            <Card className="rounded-3 mb-4">
                                <Card.Body className="p-4">
                                    <Row className="justify-content-between align-items-center">
                                        <Col md="2" lg="2" xl="2">
                                            <img src={"https://drive.google.com/thumbnail?id=" + item.project.photo} />
                                        </Col>
                                        <Col md="3" lg="3" xl="3">
                                            <p className="lead fw-normal mb-2">{item.project.name}</p>
                                            <p>
                                                <span className="text-muted">Opis: </span><br></br>
                                                {item.project.description}

                                            </p>
                                        </Col>
                                        <Col className="d-flex align-items-center justify-content-around">
                                            <Button color="link" className="px-2" onClick={() => decrease(item.project.id)} disabled={item.quantity === 1 ? true : false}>
                                                <BsDash />
                                            </Button>

                                            <Form.Control
                                                style={{ "textAlign": 'center' }}
                                                type="number"
                                                defaultValue={item.quantity}
                                            />

                                            <Button color="link" className="px-2" onClick={() => increase(item.project.id)}>
                                                <BsPlus />
                                            </Button>
                                        </Col>
                                        <Col md="3" lg="2" xl="2" className="offset-lg-1">
                                            <h3 tag="h5" className="mb-0">
                                                {item.project.price * item.quantity}zł
                                            </h3>
                                        </Col>
                                        <Col md="1" lg="1" xl="1" className="text-end">
                                            <a className="text-danger" onClick={() => deleteItem(item.project.id)}>
                                                <BsTrash />
                                            </a>
                                        </Col>
                                    </Row>
                                </Card.Body>
                            </Card>

                        );
                    })}
                    <hr style={{ 'borderTop': '1px solid white' }}></hr>
                    <Card>
                        <Card.Body>
                            <span >Suma: {totalPrice}zł</span>
                            {order?.length > 0 &&
                                <Button style={{ 'float': 'right' }} variant="success"
                                    onClick={() => open()}>
                                    Przyjmij
                                </Button>
                            }

                        </Card.Body>
                    </Card>

                    <Modal show={show} onHide={close}>
                        <Modal.Header closeButton>
                            <Modal.Title>Wybór płatności</Modal.Title>
                        </Modal.Header>
                        <Modal.Body >

                            <Row style={{ textAlign: "center" }}>
                                <Col>
                                    <CButton color="outline-secondary" style={{ margin: '10px', width: '180px', height: '50px' }} onClick={() => { payWithCash() }}>
                                        <CIcon icon={cilCash} /> Gotówka
                                    </CButton>
                                </Col>
                            </Row>
                            <Row style={{ textAlign: "center" }}>
                                <Col>
                                    <CButton color="outline-secondary" style={{ margin: '10px', width: '180px', height: '50px' }} onClick={() => { payWithPaymentGateway() }}>
                                        <CIcon icon={cilCreditCard} /> Karta
                                    </CButton>
                                </Col>
                            </Row>
                            <Row style={{ textAlign: "center" }}>
                                <Col>
                                    <CButton color="outline-secondary" style={{ margin: '10px', width: '180px', height: '50px' }} onClick={() => { payLater() }}>
                                        <CIcon icon={cilCalendar} /> Później
                                    </CButton>
                                </Col>
                            </Row>

                        </Modal.Body>
                        <Modal.Footer>
                            <CButton color="success" >
                                Zatwierdź
                            </CButton>
                        </Modal.Footer>
                    </Modal>

                </Col>
            </Row>
        </Container>

    )
}

export default Order