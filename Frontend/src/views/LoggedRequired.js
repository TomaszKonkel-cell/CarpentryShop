import React from 'react';
import { Link } from 'react-router-dom';

import {
    CContainer,
    CCol,
    CInputGroup,
    CRow
} from '@coreui/react';

const LoggedRequired = () => {

    return (

        <div className="bg-body-tertiary min-vh-100 d-flex flex-row align-items-center">
            <CContainer>
                <CRow className="justify-content-center">
                    <CCol md={6}>
                        <div className="clearfix">
                            <h1 className="float-start display-3 me-4">401</h1>
                            <h4 className="pt-3">Nieautoryzowany dostęp</h4>
                            <p className="text-body-secondary float-start">
                                Wygląda na to, że musisz się zalogować
                            </p>
                        </div>
                        <CInputGroup className="input-prepend">
                            <Link to="/Login">Zaloguj się</Link>
                        </CInputGroup>
                    </CCol>
                </CRow>
            </CContainer>
        </div>

    )
}

export default LoggedRequired