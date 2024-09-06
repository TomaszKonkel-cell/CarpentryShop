import React, { useEffect, useState } from 'react';
import { Button, Card, Col, Container, Form, Table, Row } from 'react-bootstrap';
import { useLocation, useNavigate } from "react-router-dom";

import Swal from 'sweetalert2';
import AuthOrder from '../../service/AuthOrder';
import AuthProject from '../../service/AuthProject';
import AuthUser from '../../service/AuthUser';

const ProjectDetails = () => {

    const useQuery = () => {
        return new URLSearchParams(useLocation().search);
    };
    let query = useQuery();
    let navigate = useNavigate();

    const currentUser = AuthUser.getCurrentUser();

    const [view, setView] = useState(false);
    const [quantity, setQuantity] = useState(1);
    const [project, setProject] = useState('');
    const [message, setMessage] = useState('');
    const [messageList, setMessageList] = useState({});
    const [file, setFile] = useState(null);

    const handleChange = (e) => {
        setProject({ ...project, [e.target.name]: e.target.value });
    };

    const addPhoto = (e) => {
        setFile(e.target.files[0])
    };

    const handlePut = () => {

        const postData = new FormData();
        postData.append('name', project.name);
        postData.append('price', project.price);
        postData.append('description', project.description)
        postData.append('file', file)


        Swal.fire({
            title: "Potwierdzasz nadpisanie danych?",
            showDenyButton: true,
            confirmButtonText: "TAK",
            denyButtonText: `NIE`,
            icon: "question"
        }).then((result) => {
            Swal.fire("Aktualizowanie danych");
            Swal.showLoading();
            if (result.isConfirmed) {
                AuthProject.updateProject(project.id, postData).then(
                    (res) => {
                        Swal.close()
                        Swal.fire(res.data, "", "success");
                        navigate('/ProjectsList')
                    }, (error) => {
                        Swal.close()
                        if (error.response.status === 401) {
                            setMessage(error.response.data.message)
                        } else {
                            if (typeof error.response.data == "string") {
                                setMessage(error.response.data)
                            }
                            if (typeof error.response.data == "object") {
                                setMessageList(error.response.data)
                            }
                        }
                    })
            } else if (result.isDenied) {
                Swal.fire("Anulowano nadpisywanie", "", "info");
            }
        })
    };

    const addToCart = () => {
        const item = {
            "quantity": parseInt(quantity),
            project
        }

        Swal.fire({
            title: "Potwierdzasz dodanie do zamówienia?",
            showDenyButton: true,
            confirmButtonText: "TAK",
            denyButtonText: `NIE`,
            icon: "question"
        }).then((result) => {
            if (result.isConfirmed) {
                AuthOrder.addToCart(item)
                Swal.fire({
                    icon: "success",
                    title: "Dodano do zamówienia",
                    footer: '<a href="/Order">Przejdź do zamówienia</a>'
                });
            } else if (result.isDenied) {
                Swal.fire("Anulowano wysyłanie", "", "info");
            }
        })
    };

    useEffect(() => {
        AuthProject.getProjectsDetails(query.get("id")).then(
            (res) => {
                setProject(res.data)
            });
    }, [])


    return (
        <>
            {view ? (
                <Container style={{ "marginTop": "3%" }}>
                    <Row>
                        <Col md="8">
                            <Card >
                                <Card.Header>
                                    <Card.Title as="h4">Szczegóły projektu</Card.Title>
                                </Card.Header>
                                <Card.Body>
                                    <Form>
                                        <Row>
                                            <Col className="pr-1" md="6">
                                                <Form.Group>
                                                    <label>Nazwa projektu</label>
                                                    <Form.Control
                                                        type="text"
                                                        name="name"
                                                        defaultValue={project.name}
                                                        onChange={handleChange}
                                                    ></Form.Control>
                                                </Form.Group>
                                            </Col>
                                            <Col className="pl-1" md="6">
                                                <Form.Group>
                                                    <label>Cena</label>
                                                    <Form.Control
                                                        type="text"
                                                        name="price"
                                                        defaultValue={project.price}
                                                        onChange={handleChange}
                                                    ></Form.Control>
                                                </Form.Group>
                                            </Col>

                                        </Row>
                                        <hr></hr>
                                        <Row>
                                            <Col md="12">
                                                <Form.Group>
                                                    <label>Opis</label>
                                                    <Form.Control
                                                        cols="80"
                                                        as='textarea'
                                                        name="description"
                                                        defaultValue={project.description}
                                                        onChange={handleChange}
                                                    ></Form.Control>
                                                </Form.Group>
                                            </Col>
                                        </Row>


                                        {message && (
                                            <div className="form-group">
                                                <div className="alert alert-danger" role="alert">
                                                    {message}
                                                </div>
                                            </div>
                                        )}

                                        {!message && messageList.errors && Object.values(messageList.errors).map((mes, i) => {
                                            return (
                                                <div className="form-group">
                                                    <div className="alert alert-danger" role="alert">
                                                        {mes}
                                                    </div>
                                                </div>
                                            );
                                        })}
                                    </Form>

                                    <Button
                                        style={{ "float": 'right' }}
                                        variant="info"
                                        onClick={handlePut}
                                    >
                                        Aktualizuj
                                    </Button>

                                </Card.Body>
                            </Card>
                        </Col>
                        <Col md="4">
                            <Card>
                                <Card.Header>
                                    <Card.Title as="h4">Zdjęcie</Card.Title>
                                </Card.Header>
                                <div style={{ textAlign: "center" }} >
                                    <img
                                        alt="..."
                                        src={"https://drive.google.com/thumbnail?id=" + project.photo}
                                    ></img>
                                </div>
                                <Card.Body>
                                    <Col style={{ "float": 'right' }}>
                                    Dodaj nowe zdjęcie:
                                        <Form.Group>
                                            <Form.Control
                                                type="file"
                                                name="photo"
                                                placeholder="Dodaj zdjęcie"
                                                onChange={addPhoto}
                                            ></Form.Control>
                                        </Form.Group>
                                    </Col>
                                </Card.Body>
                            </Card>
                        </Col>
                    </Row>
                </Container>
            ) : (
                <Container className='auth-wrapper'>
                    <Table striped bordered hover variant="dark">
                        <thead>
                            <tr>
                                <th>Nazwa</th>
                                <th>Cena</th>
                                <th>Opis</th>
                                <th>Akcje</th>
                            </tr>
                        </thead>

                        <tbody>
                            <tr>
                                {/* <td><Image src={`C:/Users/tkonk/Desktop/Programowanie/SpringBoot/CarpentryShop/uploads/${project.name}/${project.photo}`} rounded /></td> */}
                                <td>{project.name}</td>
                                <td>{project.price}</td>
                                <td>{project.description}</td>
                                <td>

                                    <Form.Control as='select' placeholder="Podaj ilość" onChange={(e) => {
                                        setQuantity(e.target.value)
                                    }}>
                                        <option>Domyślnia wartość - 1</option>
                                        <option value="1">1</option>
                                        <option value="2">2</option>
                                        <option value="3">3</option>
                                        <option value="4">4</option>
                                        <option value="5">5</option>
                                    </Form.Control>


                                    <hr style={{ "borderTop": "1px solid white" }}></hr>

                                    <Button variant="success" style={{ "margin": 'auto' }}
                                        onClick={() => addToCart()}>Dodaj</Button>
                                </td>
                            </tr>
                        </tbody>
                    </Table>

                    {(currentUser && currentUser.roles.includes("ROLE_ADMIN")) || (currentUser && currentUser.roles.includes("ROLE_MODERATOR")) ? (
                        <Button style={{ "float": 'right' }} onClick={() => setView(true)}>Zmiana danych</Button>
                    ) : (
                        <></>
                    )}

                </Container>


            )}
        </>



    )
}

export default ProjectDetails