import React, { useEffect, useState } from 'react';
import { Accordion, Button, Col, Container, Row, Tabs, Tab, Modal } from 'react-bootstrap';

import 'bootstrap/dist/css/bootstrap.css';

import AuthJobs from '../../service/AuthJobs';
import Swal from 'sweetalert2';
import AuthOrder from '../../service/AuthOrder';

import { CButton } from '@coreui/react';
import { CIcon } from '@coreui/icons-react';
import { cilCash, cilCreditCard } from '@coreui/icons';

const JobList = () => {
    const [jobs, setJobs] = useState();
    const [jobToPay, setJobToPay] = useState();
    const [show, setShow] = useState(false);


    const open = (id) => {
        setShow(true);
        AuthOrder.getOrderById(id).then(
            (res) => {
                setJobToPay(res.data)
            });
    }

    const close = () => setShow(false);

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
                AuthOrder.changePaidStatus(jobToPay.id).then(
                    (result) => {
                        Swal.close();
                        Swal.fire(result.data, "", "success");
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
        AuthOrder.createPaymentToLater(jobToPay).then(
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

    useEffect(() => {
        AuthJobs.getCurrentJobs().then((res) => {
            setJobs(res.data)
        })
    }, [])

    return (

        <Container className="py-5 h-100">
            <Row className="justify-content-center align-items-center h-100">
                <Col md="10">
                    <div className="d-flex justify-content-between align-items-center mb-4">
                        <h1 tag="h3" className="fw-normal mb-0 text-black">
                            Lista przyjętych zleceń
                        </h1>
                    </div>
                    <Tabs
                        justify
                    >
                        <Tab eventKey="home" title="Do zrobienia">
                            {jobs && jobs.sort((a, b) => a.id < b.id ? 1 : -1).map((job) => {
                                if (job.done === false) {
                                    return (
                                        <Accordion style={{ "margin": '10px' }}>
                                            <Accordion.Item eventKey="0">
                                                <Accordion.Header>ID zamówienia: {job.id}</Accordion.Header>
                                                <Accordion.Body>
                                                    {job.projects && job.projects.map((item) => {
                                                        if (item.resources.length == 0) {
                                                            return (
                                                                <div >
                                                                    <span >{item.project.name} x {item.quantity} </span> <Button style={{ width:"95px", "float": 'right' }} href={`/JobDetails?id=${item.id}`}>Uzupełnij</Button>
                                                                    <p>Brak dodanych zasobów</p>
                                                                    <hr style={{ "borderTop": "1px solid dark" }}></hr>
                                                                </div>
                                                            );
                                                        } else {
                                                            return (
                                                                <div >
                                                                    <span >{item.project.name} x {item.quantity} </span> <Button style={{ float: 'right' }} href={`/JobDetails?id=${item.id}`}>Uzupełnij</Button>

                                                                    {item.resources && item.resources.map((resource) => {
                                                                        return (
                                                                            <li style={{ "marginLeft": "15px" }}>
                                                                                {resource.item.itemName} x {resource.quantity}
                                                                            </li>
                                                                        );
                                                                    })}
                                                                    <hr style={{ "borderTop": "1px solid dark" }}></hr>
                                                                </div>
                                                            );
                                                        }

                                                    })}

                                                    <div >
                                                        <span>Suma: </span> {job.totalPrice} zł
                                                        {!job.paid &&
                                                            <Button style={{ width:"95px", "float": 'right' }} onClick={() => open(job.id)}>Płatność</Button>
                                                        }
                                                    </div>
                                                </Accordion.Body>
                                            </Accordion.Item>
                                        </Accordion>
                                    );
                                }

                            })}
                        </Tab>
                        <Tab eventKey="profile" title="Zrobione ">
                            {jobs && jobs.sort((a, b) => a.id < b.id ? 1 : -1).map((job) => {
                                if (job.done === true) {
                                    return (
                                        <Accordion style={{ "margin": '10px' }}>
                                            <Accordion.Item eventKey="0">
                                                <Accordion.Header>ID zamówienia: {job.id}</Accordion.Header>
                                                <Accordion.Body>
                                                    {job.projects && job.projects.map((item) => {

                                                        return (
                                                            <div >
                                                                <span >{item.project.name} x {item.quantity} </span> <Button style={{ "float": 'right' }} href={`/JobDetails?id=${item.id}`}>Szczegóły</Button>
                                                                {item.resources && item.resources.map((resource) => {
                                                                    return (
                                                                        <li style={{ "marginLeft": "15px" }}>
                                                                            {resource.item.itemName} x {resource.quantity}
                                                                        </li>
                                                                    );
                                                                })}
                                                                <hr style={{ "borderTop": "1px solid dark" }}></hr>
                                                            </div>
                                                        );

                                                    })}
                                                    <div style={{ "textAlign": 'right' }}>
                                                        <span>Suma: </span> {job.totalPrice} zł
                                                    </div>
                                                </Accordion.Body>
                                            </Accordion.Item>
                                        </Accordion>
                                    );
                                }

                            })}
                        </Tab>
                    </Tabs>

                    <Modal show={show} onHide={close}>
                        <Modal.Header closeButton>
                            <Modal.Title>Wybór płatności</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>

                            <Row style={{ textAlign: "center" }}>
                                <Col>
                                    <CButton color="outline-secondary" style={{ margin: '10px', width: '180px', height: '50px' }} onClick={() => { payWithCash() }}>
                                        <CIcon icon={cilCash} />Gotówka
                                    </CButton>
                                </Col>
                            </Row>
                            <Row style={{ textAlign: "center" }}>
                                <Col>
                                    <CButton color="outline-secondary" style={{ margin: '10px', width: '180px', height: '50px' }} onClick={() => { payWithPaymentGateway() }}>
                                        <CIcon icon={cilCreditCard} />Karta
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
        </Container >



    )
}

export default JobList