import React from 'react';

import 'bootstrap/dist/css/bootstrap.min.css';
import { useState } from 'react';
import { Container } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

import { cilLineSpacing, cilLockLocked, cilUser } from '@coreui/icons';
import CIcon from '@coreui/icons-react';
import {
    CButton,
    CCard,
    CCardBody,
    CCardGroup,
    CCol,
    CForm,
    CFormInput,
    CFormSelect,
    CInputGroup,
    CInputGroupText,
    CRow
} from '@coreui/react';

import Swal from 'sweetalert2';
import AuthUser from '../../service/AuthUser';

const CreateUser = () => {

    let navigate = useNavigate();
    const [message, setMessage] = useState('');
    const [messageList, setMessageList] = useState({});
    const [formData, setFormData] = useState({
        username: '',
        password: '',
    });

    const [roles, setRoles] = useState([]);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const addRole = (e) => {
        if (!roles.includes(e.target.value)) {
            setRoles([...roles, e.target.value])
        }

    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const postData = new FormData();
        postData.append('username', formData.username);
        postData.append('password', formData.password);
        postData.append('roles', roles)

        Swal.fire({
            title: "Potwierdzasz tworzenie?",
            showDenyButton: true,
            confirmButtonText: "TAK",
            denyButtonText: `NIE`,
            icon: "question"
        }).then((result) => {

            Swal.fire("Tworzenie użytkownika");
            Swal.showLoading();
            if (result.isConfirmed) {
                AuthUser.register(postData).then(
                    () => {
                        Swal.fire("Użytkownik stworzony pomyślnie","", "success");
                        navigate("/UsersList")
                    },
                    (error) => {
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
        
                    }
                );
            } else if (result.isDenied) {
                Swal.fire("Anulowano zapisywanie", "", "info");
            }
        })

    };


    return (
        <Container className="auth-wrapper">
                <CRow className="justify-content-center">
                    <CCol md={4}>
                        <CCardGroup>
                            <CCard className="p-4">
                                <CCardBody>
                                    <CForm onSubmit={handleSubmit}>
                                        <h1>Stwórz użytkownika</h1>
                                        <p className="text-body-secondary">Podaj jego dane</p>
                                        <CInputGroup className="mb-3">
                                            <CInputGroupText>
                                                <CIcon icon={cilUser} />
                                            </CInputGroupText>
                                            <CFormInput
                                                type="text"
                                                name="username"
                                                placeholder="Wprowadź login"
                                                onChange={handleChange}
                                                value={formData.username} />
                                        </CInputGroup>
                                        <CInputGroup className="mb-4">
                                            <CInputGroupText>
                                                <CIcon icon={cilLockLocked} />
                                            </CInputGroupText>
                                            <CFormInput
                                                type="password"
                                                name="password"
                                                placeholder="Wprowadź hasło"
                                                onChange={handleChange}
                                                value={formData.password}
                                            />
                                        </CInputGroup>
                                        <CInputGroup className="mb-3">
                                            <CInputGroupText>
                                                <CIcon icon={cilLineSpacing} />
                                            </CInputGroupText>
                                            <CFormSelect onChange={addRole}>
                                                <option>Wybierz role</option>
                                                <option value="admin">admin</option>
                                                <option value="user">user</option>
                                                <option value="mod">mod</option>
                                            </CFormSelect>

                                        </CInputGroup>
                                        Wybrano:
                                        <hr style={{ "color": 'white' }}></hr>
                                        <ul>
                                            {roles.map((role) => (<li>{role}</li>))}
                                        </ul>
                                        {message && (
                                            <div className="form-group">
                                                <div className="alert alert-danger" role="alert" >
                                                    {message}
                                                </div>
                                            </div>
                                        )}


                                        {!message && messageList.errors && Object.values(messageList.errors).map((mes) => {
                                            return (
                                                <div className="form-group">
                                                    <div className="alert alert-danger" role="alert">
                                                        {mes}
                                                    </div>
                                                </div>
                                            );
                                        })}
                                        <CRow>
                                            <CCol>
                                                <CButton color="primary" className="px-4" type="submit">
                                                    Stwórz
                                                </CButton>
                                            </CCol>
                                        </CRow>
                                    </CForm>
                                </CCardBody>
                            </CCard>
                        </CCardGroup>
                    </CCol>
                </CRow>
        </Container>
    );
}


export default CreateUser
