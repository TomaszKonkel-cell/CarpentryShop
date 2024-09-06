import React from 'react';

import { cilLockLocked, cilUser } from '@coreui/icons';
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
import 'bootstrap/dist/css/bootstrap.min.css';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Swal from 'sweetalert2';

import AuthUser from '../../service/AuthUser';

const Login = () => {

  let navigate = useNavigate();
  const [message, setMessage] = useState('');
  const [messageList, setMessageList] = useState({});
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    Swal.fire("Logowanie...")
    Swal.showLoading();
    
    const postData = new FormData();
    postData.append('username', formData.username);
    postData.append('password', formData.password);

    AuthUser.login(postData).then(
      () => {
        const loggedUser = AuthUser.getCurrentUser();
        if (loggedUser.roles.includes("ROLE_ADMIN")) {
          Swal.close();
          navigate("/UsersList");
          window.location.reload()
        }
        else {
          Swal.close();
          navigate("/ProjectsList");
          window.location.reload()
        }

      },
      (error) => {
        if (error.response.status == 401) {
          Swal.close();
          setMessage(error.response.data.message)
        } else {
          if (typeof error.response.data == "string") {
            Swal.close();
            setMessage(error.response.data)
          }
          if (typeof error.response.data == "object") {
            Swal.close();
            setMessageList(error.response.data)
          }
        }

      }
    );
  };

  return (

    <CContainer className='auth-wrapper'>
      <CRow className="justify-content-center">
        <CCol md={4}>
          <CCardGroup>
            <CCard className="p-4">
              <CCardBody>
                <CForm onSubmit={handleSubmit}>
                  <h1>Logowanie</h1>
                  <p className="text-body-secondary">Wprowadź dane</p>
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
                        Zaloguj
                      </CButton>
                    </CCol>
                  </CRow>
                </CForm>
              </CCardBody>
            </CCard>
          </CCardGroup>
        </CCol>
      </CRow>
    </CContainer>

  );
}


export default Login
