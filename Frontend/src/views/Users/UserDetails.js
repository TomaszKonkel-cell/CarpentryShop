import React, { useEffect, useState } from 'react';
import { Button, Col, Form, Modal, Row } from 'react-bootstrap';
import { useLocation, useNavigate } from "react-router-dom";

import { cilUser } from '@coreui/icons';
import CIcon from '@coreui/icons-react';
import {
    CButton,
    CCard,
    CCardBody,
    CCardGroup,
    CCol,
    CContainer,
    CForm,
    CFormInput,
    CInputGroup,
    CInputGroupText,
    CRow,
} from '@coreui/react';
import Swal from 'sweetalert2';
import AuthUser from "../../service/AuthUser";

const UserDetails = () => {
    const useQuery = () => {
        return new URLSearchParams(useLocation().search);
    };

    let query = useQuery();
    let navigate = useNavigate();
    const [profile, setProfile] = useState('');
    const [show, setShow] = useState(false);
    const [username, setUsername] = useState('');
    const [formMessage, setFormMessage] = useState('');
    const [modalMessage, setModalMessage] = useState('');
    const [password, setPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');

    const open = () => setShow(true);
    const close = () => setShow(false);
    const handleChange = (e) => setUsername(e.target.value);

    const changePassForm = () => {
        Swal.fire({
            title: "Potwierdzasz zmiane hasła?",
            showDenyButton: true,
            confirmButtonText: "TAK",
            denyButtonText: `NIE`,
            icon: "question"
        }).then((result) => {
            if (result.isConfirmed) {
                AuthUser.changePassword(password, newPassword, profile.id).then(
                    (res) => {
                        setShow(false)
                        Swal.fire(res.data, "", "success");
                    },
                    (error) => {
                        setModalMessage(error.response.data)
                    }
                );
            } else if (result.isDenied) {
                Swal.fire("Anulowano zmienianie", "", "info");
            }
        })
    };



    const postChangeUsername = () => {
        Swal.fire({
            title: "Potwierdzasz zmiane nazwy?",
            showDenyButton: true,
            confirmButtonText: "TAK",
            denyButtonText: `NIE`,
            icon: "question"
        }).then((result) => {
            if (result.isConfirmed) {
                AuthUser.changeUsername(profile.id, username).then(
                    (res) => {
                        Swal.fire(res.data, "", "success");
                        navigate("/UsersList")
                    },
                    (error) => {
                        setFormMessage(error.response.data)

                    }
                );
            } else if (result.isDenied) {
                Swal.fire("Anulowano zmienianie", "", "info");
            }
        })
    };

    const deleteRole = (id, role) => {
        Swal.fire({
            title: "Potwierdzasz usunięcie roli?",
            showDenyButton: true,
            confirmButtonText: "TAK",
            denyButtonText: `NIE`,
            icon: "question"
        }).then((result) => {
            if (result.isConfirmed) {
                AuthUser.deleteRole(id, role).then(
                    (res) => {
                        Swal.fire(res.data, "", "success");
                        navigate("/UsersList")
                    },
                    (error) => {
                        setFormMessage(error.response.data)
                    }
                );
            } else if (result.isDenied) {
                Swal.fire("Anulowano zmienianie", "", "info");
            }
        })
    };

    useEffect(() => {
        AuthUser.getUsersDetails(query.get("id")).then(
            (res) => {
                setUsername(res.data.username)
                setProfile(res.data)
            });
    }, [])

    const rolesCount = profile.roles && profile.roles.filter(item => item.name).length;

    return (
        <CContainer className='auth-wrapper'>
            <CRow className="justify-content-center">
                <CCol md={4}>
                    <CCardGroup>
                        <CCard className="p-4">
                            <CCardBody>
                                <CForm>
                                    <h1>Dane użytkownika</h1>
                                    <p className="text-body-secondary">Wprowadź dane</p>
                                    <CInputGroup className="mb-3">
                                        <CInputGroupText>
                                            <CIcon icon={cilUser} />
                                        </CInputGroupText>
                                        <CFormInput
                                            type="text"
                                            name="username"
                                            defaultValue={profile.username}
                                            onChange={handleChange} />
                                    </CInputGroup>

                                    {formMessage && (
                                        <div className="form-group">
                                            <div className="alert alert-danger" role="alert">
                                                {formMessage}
                                            </div>
                                        </div>
                                    )}

                                    {profile.roles && profile.roles.map((role) => {
                                        return (
                                            <Button disabled={rolesCount === 1}
                                                variant={rolesCount == 1 ? "outline-primary" : "outline-danger"}
                                                className={rolesCount == 1 ? "" : "bi bi-x"}
                                                style={{ margin: '10px', width: '180px', height: '50px' }}
                                                onClick={() => { deleteRole(profile.id, role.name) }}>{role.name}
                                            </Button>

                                        )
                                    })}

                                    <hr style={{ "color": 'white' }}></hr>


                                    <Modal show={show} onHide={close}>
                                        <Modal.Header closeButton>
                                            <Modal.Title>Zmienianie hasła</Modal.Title>
                                        </Modal.Header>
                                        <Modal.Body>
                                            <Row >
                                                <Form.Group as={Col} md="12">
                                                    <Form.Label>Stare hasło</Form.Label>
                                                    <Form.Control
                                                        type="password"
                                                        name="oldPass"
                                                        onChange={(e) => { setPassword(e.target.value) }}
                                                    />
                                                </Form.Group>
                                            </Row>

                                            <Row >
                                                <Form.Group as={Col} md="12">
                                                    <Form.Label>Nowe hasło</Form.Label>
                                                    <Form.Control
                                                        type="password"
                                                        name="newPass"
                                                        onChange={(e) => { setNewPassword(e.target.value) }}
                                                    />
                                                </Form.Group>
                                            </Row>

                                            {modalMessage && (
                                                <div className="form-group">
                                                    <div className="alert alert-danger" role="alert">
                                                        {modalMessage}
                                                    </div>
                                                </div>
                                            )}
                                        </Modal.Body>
                                        <Modal.Footer>
                                            <Button variant="primary" onClick={changePassForm}>
                                                Zatwierdź
                                            </Button>
                                        </Modal.Footer>
                                    </Modal>

                                    <CRow>
                                        <CCol>
                                            <CButton color='success' onClick={open}>Zmień hasło</CButton>
                                        </CCol>
                                        <CCol>
                                            <CButton color='primary' onClick={postChangeUsername}>Zmień nazwe</CButton>
                                        </CCol>
                                    </CRow>
                                </CForm>
                            </CCardBody>
                        </CCard>
                    </CCardGroup>
                </CCol>
            </CRow>

        </CContainer>



    )
}

export default UserDetails