import React from 'react';

import { cilDescription, cilLockLocked, cilUser } from '@coreui/icons';
import CIcon from '@coreui/icons-react';
import {
    CButton,
    CCard,
    CCardBody,
    CCardGroup,
    CCol,
    CForm,
    CFormInput,
    CFormTextarea,
    CInputGroup,
    CInputGroupText,
    CRow
} from '@coreui/react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useState } from 'react';
import { Container, Form } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

import Swal from 'sweetalert2';
import AuthProject from '../../service/AuthProject';

const CreateProject = () => {

    let navigate = useNavigate();

    const [message, setMessage] = useState('');
    const [messageList, setMessageList] = useState({});
    const [file, setFile] = useState();
    const [formData, setFormData] = useState({
        name: '',
        price: 0,
        description: ''
    });

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const addPhoto = (e) => {
        setFile(e.target.files[0])
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        const postData = new FormData();
        postData.append('name', formData.name);
        postData.append('price', formData.price);
        postData.append('file', file)
        postData.append('description', formData.description)

        Swal.fire({
            title: "Potwierdzasz tworzenie?",
            showDenyButton: true,
            confirmButtonText: "TAK",
            denyButtonText: `NIE`,
            icon: "question"
        }).then((result) => {

            Swal.fire("Zapisywanie danych");
            Swal.showLoading();
            if (result.isConfirmed) {
                AuthProject.createProject(postData).then(
                    (res) => {
                        Swal.close();
                        Swal.fire(res.data, "", "success");
                        navigate("/ProjectsList")
                    },
                    (error) => {
                        Swal.close();
                        if (error.response.status == 401) {
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
                )
            } else if (result.isDenied) {
                Swal.fire("Anulowano zapisywanie", "", "info");
            }
        })

    };

    return (
        <Container className="auth-wrapper">
            <CRow className="justify-content-center">
                <CCol md={8}>
                    <CCardGroup>
                        <CCard className="p-4">
                            <CCardBody>
                                <CForm onSubmit={handleSubmit}>
                                    <h1>Stwórz projekt</h1>
                                    <p className="text-body-secondary">Podaj jego dane</p>

                                    <CInputGroup className="mb-3">
                                        <CInputGroupText>
                                            <CIcon icon={cilUser} />
                                        </CInputGroupText>
                                        <CFormInput
                                            type="text"
                                            name="name"
                                            placeholder="Wprowadź nazwe"
                                            onChange={handleChange}
                                            value={formData.name}
                                        />
                                    </CInputGroup>

                                    <CInputGroup className="mb-4">
                                        <CInputGroupText>
                                            <CIcon icon={cilLockLocked} />
                                        </CInputGroupText>
                                        <CFormInput
                                            type="number"
                                            name="price"
                                            placeholder="Wprowadź cene"
                                            onChange={handleChange}
                                        />
                                    </CInputGroup>

                                    <Form.Group className="mb-4">
                                        <Form.Control 
                                        type="file"
                                            name="photo"
                                            placeholder="Dodaj zdjęcie"
                                            onChange={addPhoto}
                                            value={formData.photo}
                                        />
                                    </Form.Group>

                                    <CInputGroup className="mb-4">
                                        <CInputGroupText>
                                            <CIcon icon={cilDescription} />
                                        </CInputGroupText>
                                        <CFormTextarea
                                            type="text"
                                            name="description"
                                            placeholder="Wprowadź opis"
                                            onChange={handleChange}
                                            value={formData.description}
                                        />
                                    </CInputGroup>

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


export default CreateProject
