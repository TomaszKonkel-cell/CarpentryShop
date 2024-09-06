import React from 'react';

import 'bootstrap/dist/css/bootstrap.min.css';
import { useState } from 'react';
import { Container } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

import { cilDescription, cilLineSpacing, cilLockLocked, cilUser } from '@coreui/icons';
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
    CFormTextarea,
    CInputGroup,
    CInputGroupText,
    CRow
} from '@coreui/react';

import Swal from 'sweetalert2';
import AuthStorage from '../../service/AuthStorage';

const CreateStorageItem = () => {

    let navigate = useNavigate();
    const [message, setMessage] = useState('');
    const [messageList, setMessageList] = useState({});
    const [formData, setFormData] = useState({
        itemName: '',
        quantity: 0,
        description: '',
        itemCode: '',
        type: '',
        categories: ''
    });

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const postData = new FormData();
        postData.append('itemName', formData.itemName);
        postData.append('quantity', formData.quantity);
        postData.append('description', formData.description)
        postData.append('type', formData.type)
        postData.append('categories', formData.categories)

        Swal.fire({
            title: "Potwierdzasz tworzenie?",
            showDenyButton: true,
            confirmButtonText: "TAK",
            denyButtonText: `NIE`,
            icon: "question"
        }).then((result) => {
            if (result.isConfirmed) {
                AuthStorage.createStorageItem(postData).then(
                    (res) => {
                        Swal.fire(res.data, "", "success");
                        navigate("/Storage")
                    },
                    (error) => {
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
                                    <h1>Stwórz przedmiot</h1>
                                    <p className="text-body-secondary">Podaj jego dane</p>

                                    <CInputGroup className="mb-3">
                                        <CInputGroupText>
                                            <CIcon icon={cilUser} />
                                        </CInputGroupText>
                                        <CFormInput
                                            type="text"
                                            name="itemName"
                                            placeholder="Wprowadź nazwe"
                                            onChange={handleChange}
                                            value={formData.itemName}
                                        />
                                    </CInputGroup>

                                    <CInputGroup className="mb-4">
                                        <CInputGroupText>
                                            <CIcon icon={cilLockLocked} />
                                        </CInputGroupText>
                                        <CFormInput
                                            type="number"
                                            name="quantity"
                                            placeholder="Wprowadź ilość"
                                            onChange={handleChange}
                                        />
                                    </CInputGroup>

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

                                    <CInputGroup className="mb-3">
                                        <CInputGroupText>
                                            <CIcon icon={cilLineSpacing} />
                                        </CInputGroupText>
                                        <CFormSelect name="type"onChange={handleChange}>
                                            <option>Wybierz typ</option>
                                            <option value="LIQUID">Płynny</option>
                                            <option value="CONSTANT">Stały</option>
                                        </CFormSelect>

                                    </CInputGroup>

                                    <CInputGroup className="mb-3">
                                        <CInputGroupText>
                                            <CIcon icon={cilLineSpacing} />
                                        </CInputGroupText>
                                        <CFormSelect name="categories" onChange={handleChange}>
                                            <option>Wybierz kategorie</option>
                                            <option value="WOOD">Drewniany</option>
                                            <option value="METAL">Metalowy</option>
                                            <option value="PAINT">Farba</option>
                                        </CFormSelect>

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
            {/* <Card bg='dark' className="shadow p-3 mb-5 bg-white rounded">
                <Container>
                        <Form onSubmit={handleSubmit}>

                                <Form.Group as={Col} md="12" controlId="validationCustom01">
                                    <Form.Label>Nazwa Przedemiotu</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="itemName"
                                        placeholder="Wprowadź nazwe"
                                        onChange={handleChange}
                                        value={formData.itemName}
                                    />
                                </Form.Group>

                                <Form.Group as={Col} md="12" controlId="validationCustom02">
                                    <Form.Label>Ilość</Form.Label>
                                    <Form.Control
                                        type="number"
                                        name="quantity"
                                        placeholder="Wprowadź ilość"
                                        onChange={handleChange}
                                    />
                                </Form.Group>

                                <Form.Group as={Col} md="12" controlId="validationCustom02">
                                    <Form.Label>Opis</Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        name="description"
                                        placeholder="Wprowadź opis"
                                        onChange={handleChange}
                                        value={formData.description}
                                    />
                                </Form.Group>

                                <Form.Group as={Col} md="12" controlId="validationCustom03">
                                    <Form.Label>Typ przedmiotu</Form.Label>
                                    <Form.Control as='select'
                                        name="type"
                                        onChange={handleChange} >
                                        <option>Wybierz typ</option>
                                        <option value="LIQUID">Płynny</option>
                                        <option value="CONSTANT">Stały</option>
                                    </Form.Control>
                                </Form.Group>

                                <Form.Group as={Col} md="12" controlId="validationCustom03">
                                    <Form.Label>Kategoria przedmiotu</Form.Label>
                                    <Form.Control as='select'
                                        name="categories"
                                        onChange={handleChange} >
                                        <option>Wybierz kategorie</option>
                                        <option value="WOOD">Drewniany</option>
                                        <option value="METAL">Metalowy</option>
                                        <option value="PAINT">Farba</option>
                                    </Form.Control>
                                </Form.Group>



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
                            <div className="button">
                                <Button style={{ "float": 'right' }} type="submit">Stwórz</Button>

                            </div>

                        </Form>
                </Container>
            </Card> */}
        </Container>

    );
}


export default CreateStorageItem
