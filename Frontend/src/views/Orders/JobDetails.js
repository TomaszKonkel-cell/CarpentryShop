import React, { useEffect, useState } from 'react';
import { Button, Col, Container, Form, Row, Table } from 'react-bootstrap';
import { BsTrash } from "react-icons/bs";
import { useLocation, useNavigate } from "react-router-dom";
import Swal from 'sweetalert2';

import AuthJobs from '../../service/AuthJobs';
import AuthStorage from '../../service/AuthStorage';

const JobDetails = () => {
    const useQuery = () => {
        return new URLSearchParams(useLocation().search);
    };

    const [job, setJob] = useState('');
    const [storage, setStorage] = useState('');
    const [quantity, setQuantity] = useState(1);
    const [item, setItem] = useState({});
    let query = useQuery();
    let navigate = useNavigate();

    const selected = AuthJobs.getResourcesForJob(query.get("id"));

    const restore = () => {
        Swal.fire("Przywracanie zlecenia")
        Swal.showLoading();
        AuthJobs.deleteResourcesFromDatabase(job.id).then(
            async (result) => {
                Swal.close();
                AuthJobs.restoreToLocal(job)
                Swal.fire(result.data, "", "success");
            },
            (error) => {
                Swal.close();
                Swal.fire(error.response.data, "", "error");
            }
        );
    }

    const accept = () => {
        Swal.fire("Dodawanie do zlecenia")
        Swal.showLoading();
        if (selected.length > 0) {
            AuthJobs.addResources(query.get("id")).then(
                async (result) => {
                    AuthJobs.deleteFromLocal(query.get("id"))
                    if ((await AuthJobs.closeOrder(query.get("id"))).data === true) {
                        Swal.close();
                        Swal.fire("Dodano zasoby do wszystkich pozycji, zamówienie zostanie zamknięte", "", "success");
                        navigate(`/JobList`)
                    } else {
                        Swal.close();
                        Swal.fire(result.data, "", "success");
                        navigate(`/JobList`)
                    }

                },
                (error) => {
                    Swal.close();
                    Swal.fire(error.response.data, "", "error");
                }
            );
        } else {
            Swal.close();
            Swal.fire("Brak dodanych zasobów dla zlecenia", "", "error");
        }
    }

    const deleteResource = (name) => {
        AuthJobs.deleteResource(query.get("id"), name).then(
            (result) => {
                Swal.fire(result, "", "success");
                navigate(`/JobDetails?id=${query.get("id")}`)
            })
    }

    const handleChangeSelect = (e) => {
        setItem(e)
    }

    const handleChangeQuantity = (e) => {
        setQuantity(e)
    }

    useEffect(() => {
        AuthJobs.getJobDetails(query.get("id")).then(
            (res) => {
                setJob(res.data)
            });
        AuthStorage.getStorage().then(
            (res) => {
                setStorage(res.data)
            });
    })
    return (
        <>
            <Container className='auth-wrapper'>
                <Table striped bordered hover variant="dark">
                    <thead>
                        <tr>
                            <th>Nazwa pozycji</th>
                            <th>Ilość</th>
                            <th>Wykorzystane</th>
                            <th>Podsumowanie</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            {/* <td><Image src={`C:/Users/tkonk/Desktop/Programowanie/SpringBoot/CarpentryShop/uploads/${project.name}/${project.photo}`} rounded /></td> */}
                            <td>{job && job.project.name}</td>
                            <td>{job.quantity}</td>
                            <td>
                                <Row className="justify-content-center">
                                    <Col xs="12">
                                        <Form.Control as='select' onChange={(e) => {
                                            handleChangeSelect(e.target.value)
                                        }} disabled={job.resources && Object.keys(job.resources).length > 0 ? true : false}>
                                            <option>Wybierz przedmiot ...</option>
                                            {storage && storage.map((item) => {
                                                return (
                                                    <option value={JSON.stringify(item)}>{item.itemName} - {item.itemCode} x {item.qunatity}</option>
                                                );
                                            })}
                                        </Form.Control>
                                        <br></br>
                                        <Form.Control as='select' placeholder="Podaj ilość" onChange={(e) => {
                                            handleChangeQuantity(e.target.value)
                                        }} disabled={job.resources && Object.keys(job.resources).length > 0 ? true : false}>
                                            <option>Domyślnia wartość - 1</option>
                                            <option value="1">1</option>
                                            <option value="2">2</option>
                                            <option value="3">3</option>
                                            <option value="4">4</option>
                                            <option value="5">5</option>
                                        </Form.Control>
                                        <hr style={{ "borderTop": "1px solid white" }}></hr>
                                        <Button variant="success" style={{ "margin": 'auto' }}
                                            onClick={() => {
                                                if (Object.keys(item).length === 0) {
                                                    Swal.fire("Należy wybrać przedmiot", "", "error");
                                                } else {
                                                    const resource = {
                                                        "id": job.id,
                                                        "quantity": parseInt(quantity),
                                                        "item": JSON.parse(item)
                                                    }
                                                    AuthJobs.addResourceToLocal(resource).then(
                                                        (result) => {
                                                            Swal.fire(result, "", "success");
                                                            navigate(`/JobDetails?id=${query.get("id")}`)
                                                        })
                                                }


                                            }} disabled={job.resources && Object.keys(job.resources).length > 0 ? true : false}>Dodaj</Button>
                                    </Col>
                                </Row>
                            </td>
                            <td>

                                {job.resources && job.resources.map((resource) => {
                                    return (

                                        <Row>
                                            <Col style={{ "textAlign": "center" }}>
                                                <p>{resource.item.itemName}  x  {resource.quantity}</p>
                                            </Col>
                                        </Row>

                                    );
                                })}

                                {selected && selected.map((select) => {
                                    return (
                                        <div>
                                            {select.resources && select.resources.map((resource) => {
                                                return (

                                                    <Row>
                                                        <Col style={{ "textAlign": "left" }}>
                                                            <p>{resource.item.itemName}  x  {resource.quantity}</p>
                                                        </Col>
                                                        <Col style={{ "textAlign": "right" }}>
                                                            <a className='text-danger' onClick={() => deleteResource(resource.item.itemName) }>
                                                                <BsTrash />
                                                            </a>
                                                        </Col>
                                                    </Row>

                                                );
                                            })}
                                        </div>

                                    );

                                })}

                            </td>

                        </tr>
                    </tbody>
                    <tfoot>
                        <tr>
                            <td colSpan="4" style={{ "textAlign": "right" }}>

                                {job.resources && Object.keys(job.resources).length > 0 ? (
                                    <Button variant="success" onClick={() => { restore() }}>Przywróc</Button>
                                ) : (
                                    <Button onClick={() => { accept() }}>Zatwierdź</Button>
                                )
                                }
                            </td>

                        </tr>
                    </tfoot>


                </Table>
            </Container >
        </>



    )
}

export default JobDetails