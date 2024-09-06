import '@progress/kendo-theme-material/dist/all.css';
import 'bootstrap-daterangepicker/daterangepicker.css';
import 'bootstrap/dist/css/bootstrap.css';

import {
    CCard,
    CCardBody,
    CCol,
    CForm,
    CFormInput,
    CFormLabel
} from '@coreui/react';
import { Button } from '@progress/kendo-react-buttons';
import React, { useState } from 'react';
import { Modal } from 'react-bootstrap';

import Swal from 'sweetalert2';
import AuthMail from '../service/AuthMail';

const MailForm = (props) => {
    const [formData, setFormData] = useState({
        email: '',
        subject: '',
    });

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = () => {
        Swal.fire({
            title: "Potwierdzasz wysłanie wiadomości?",
            showDenyButton: true,
            confirmButtonText: "TAK",
            denyButtonText: `NIE`,
            icon: "question"
        }).then((result) => {
            Swal.fire("Wysyłanie wiadomości")
            Swal.showLoading();
            if (result.isConfirmed) {
                AuthMail.sendMail(formData.email, formData.subject, props.startDate, props.endDate).then(
                    (res) => {
                        Swal.close()
                        Swal.fire(res.data, "", "success");
                    }, (error) => {
                        Swal.close()
                        Swal.fire(error.response.data, "", "error");
                    })
            } else if (result.isDenied) {
                Swal.fire("Anulowano wysyłanie", "", "info");
            }
        })}
        
        return (

            <Modal show={props.mail} onHide={() => window.location.reload()}>
                <Modal.Header closeButton>
                    <Modal.Title>Uzupełnij dane</Modal.Title>
                </Modal.Header>
                <Modal.Body>

                    <CCol xs={12}>
                        <CCard className="mb-4">
                            <CCardBody>
                                <CForm >
                                    <div className="mb-3">
                                        <CFormLabel >Wyślij do:</CFormLabel>
                                        <CFormInput
                                            type="email"
                                            name="email"
                                            placeholder="name@example.com"
                                            onChange={handleChange}
                                        />
                                    </div>
                                    <div className="mb-3">
                                        <CFormLabel >Temat: </CFormLabel>
                                        <CFormInput
                                            name="subject"
                                            placeholder="Lista utargu"
                                            onChange={handleChange}
                                        />
                                    </div>

                                    <div className="mb-3">
                                        <CFormLabel >Zakres dat:</CFormLabel>
                                        <p>
                                            <strong>{props.startDate}</strong> - <strong>{props.endDate}</strong>
                                        </p>
                                    </div>
                                </CForm>
                            </CCardBody>
                        </CCard>
                    </CCol>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={() => window.location.reload()}>
                        Wróc
                    </Button>
                    <Button variant="success" onClick={handleSubmit}>
                        Wyślij raport
                    </Button>
                </Modal.Footer>
            </Modal>

        )
    }

    export default MailForm