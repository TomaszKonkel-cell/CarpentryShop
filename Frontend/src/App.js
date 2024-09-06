import React, { Suspense } from 'react';
import { Route, Routes } from 'react-router-dom';

import Header from './components/Header';
import './scss/style.scss';
import LoggedRequired from './views/LoggedRequired';
import NotAllowedPage from './views/NotAllowedPage';

import 'bootstrap/dist/css/bootstrap.min.css';
import Swal from 'sweetalert2';
import AuthUser from './service/AuthUser';


const Home = React.lazy(() => import('./views/Home'))
const Login = React.lazy(() => import('./views/Users/Login'))
const CreateUser = React.lazy(() => import('./views/Users/CreateUser'))
const UsersList = React.lazy(() => import('./views/Users/UsersList'))
const UserDetails = React.lazy(() => import('./views/Users/UserDetails'))

const CreateProject = React.lazy(() => import('./views/Projects/CreateProject'))
const ProjectsList = React.lazy(() => import('./views/Projects/ProjectsList'))
const ProjectDetails = React.lazy(() => import('./views/Projects/ProjectDetails'))

const CreateStorageItem = React.lazy(() => import('./views/Storage/CreateStorageItem'))
const Storage = React.lazy(() => import('./views/Storage/Storage'))
const StorageItemDetails = React.lazy(() => import('./views/Storage/StorageItemDetails'))

const Order = React.lazy(() => import('./views/Orders/Order'))
const JobList = React.lazy(() => import('./views/Orders/JobList'))
const JobDetails = React.lazy(() => import('./views/Orders/JobDetails'))
const Success = React.lazy(() => import('./views/Payment/Success'))

const App = () => {
  const currentUser = AuthUser.getCurrentUser();

  var now = new Date().getTime();
  if (currentUser != null) {
    if (now - currentUser.loginTime > 3600000) {
      localStorage.removeItem('user')
      Swal.fire("Sesja wygasła wymagane jest ponowne zalogowanie się", "", "error");
    }
  }

  return (
    <div >
      <link
        rel="stylesheet"
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
        integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
        crossOrigin="anonymous"
      />

      <Header />
      <Suspense fallback={<div>Ładowanie...</div>}>
        <Routes>
          <Route path="/" element={!currentUser ? (<Login />) : (<Home />)} />
          <Route path="/Login" element={!currentUser ? (<Login />) : (<Home />)} />

          <Route path="/Home" element={currentUser ? (<Home />) : (<LoggedRequired />)} />
          <Route path="/ProjectsList" element={currentUser ? (<ProjectsList />) : (<LoggedRequired />)} />
          <Route path="/ProjectDetails" element={currentUser ? (<ProjectDetails />) : (<LoggedRequired />)} />
          <Route path="/Order" element={currentUser ? (<Order />) : (<LoggedRequired />)} />
          <Route path="/JobList" element={currentUser ? (<JobList />) : (<LoggedRequired />)} />
          <Route path="/JobDetails" element={currentUser ? (<JobDetails />) : (<LoggedRequired />)} />
          <Route path="/Success" element={currentUser ? (<Success />) : (<LoggedRequired />)} />

          <Route path="/CreateProject" element={(currentUser && currentUser.roles.includes("ROLE_ADMIN")) || (currentUser && currentUser.roles.includes("ROLE_MODERATOR")) ? (<CreateProject />) : (<NotAllowedPage />)} />
          <Route path="/Storage" element={(currentUser && currentUser.roles.includes("ROLE_ADMIN")) || (currentUser && currentUser.roles.includes("ROLE_MODERATOR")) ? (<Storage />) : (<NotAllowedPage />)} />
          <Route path="/CreateStorageItem" element={(currentUser && currentUser.roles.includes("ROLE_ADMIN")) || (currentUser && currentUser.roles.includes("ROLE_MODERATOR")) ? (<CreateStorageItem />) : (<NotAllowedPage />)} />
          <Route path="/StorageItemDetails" element={(currentUser && currentUser.roles.includes("ROLE_ADMIN")) || (currentUser && currentUser.roles.includes("ROLE_MODERATOR")) ? (<StorageItemDetails />) : (<NotAllowedPage />)} />

          <Route path="/UsersList" element={currentUser && currentUser.roles.includes("ROLE_ADMIN") ? (<UsersList />) : (<NotAllowedPage />)} />
          <Route path="/CreateUser" element={currentUser && currentUser.roles.includes("ROLE_ADMIN") ? (<CreateUser />) : (<NotAllowedPage />)} />
          <Route path="/UserDetails" element={currentUser && currentUser.roles.includes("ROLE_ADMIN") ? (<UserDetails />) : (<NotAllowedPage />)} />
        </Routes>
      </Suspense>
    </div>
  );
}

export default App;
