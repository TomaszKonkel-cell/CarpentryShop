import React from 'react';
import { Link } from 'react-router-dom';

import {
    CContainer,
    CCol,
    CInputGroup,
    CRow
} from '@coreui/react';


const NotAllowedPage = () => {

    return (

        <div className="bg-body-tertiary min-vh-100 d-flex flex-row align-items-center">
            <CContainer>
                <CRow className="justify-content-center">
                    <CCol md={6}>
                        <div className="clearfix">
                            <h1 className="float-start display-3 me-4">403</h1>
                            <h4 className="pt-3">Niedozwolony dostęp</h4>
                            <p className="text-body-secondary float-start">
                                Wygląda na to, że nie posiadasz uprawnień do tych zasobów
                            </p>
                        </div>
                        <CInputGroup className="input-prepend">
                            <Link to="/Home">Powrót</Link>
                        </CInputGroup>
                    </CCol>
                </CRow>
            </CContainer>
        </div>

    )
}

export default NotAllowedPage