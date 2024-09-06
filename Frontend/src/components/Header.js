import React from 'react';
import { Container, Nav, Navbar, NavDropdown } from "react-bootstrap";
import { useNavigate } from 'react-router-dom';

import AuthUser from '../service/AuthUser';

const Header = () => {
    const currentUser = AuthUser.getCurrentUser();

    let navigate = useNavigate();
    const logout = () => {
        AuthUser.logout()
        navigate('/Login')
        window.location.reload();
    };

    return (
        <Navbar bg="light" expand="lg">
            <Container fluid>
                <div className="d-flex justify-content-center align-items-center ml-2 ml-lg-0">
                    <Navbar.Brand
                        href="/home"
                        className="mr-2"
                    >
                        CarpentryShop
                    </Navbar.Brand>
                </div>
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="ml-auto" navbar>
                        {currentUser &&
                            <Nav.Item>
                                <Nav.Link
                                    className="m-0"
                                    href="/Order"
                                >
                                    Zamówienie
                                </Nav.Link>
                            </Nav.Item>
                        }
                        {currentUser &&
                            <Nav.Item>
                                <Nav.Link
                                    className="m-0"
                                    href="/JobList"
                                >
                                    Zlecenia
                                </Nav.Link>
                            </Nav.Item>
                        }
                        {currentUser &&
                            <NavDropdown
                                id="nav-dropdown-dark-example"
                                title="Przenieś do..."
                            >
                                {currentUser && currentUser.roles.includes("ROLE_ADMIN") &&
                                    <NavDropdown.Item href="/UsersList">
                                        <span className="no-icon">Users</span>
                                    </NavDropdown.Item>
                                }

                                {currentUser &&
                                    <NavDropdown.Item href="/ProjectsList">
                                        <span className="no-icon">Projekty</span>
                                    </NavDropdown.Item>
                                }
                                {(currentUser && currentUser.roles.includes("ROLE_ADMIN")) || (currentUser && currentUser.roles.includes("ROLE_MODERATOR")) ? (
                                    <NavDropdown.Item href="/Storage">
                                        <span className="no-icon">Lista magazynowa</span>
                                    </NavDropdown.Item>
                                ) : (
                                    <></>
                                )}


                            </NavDropdown>
                        }



                        {currentUser ? (
                            <Nav.Item>
                                <Nav.Link
                                    className="m-0"
                                    onClick={logout}
                                >
                                    <span className="no-icon">Logout</span>
                                </Nav.Link>
                            </Nav.Item>
                        ) : (
                            <Nav.Item>
                                <Nav.Link
                                    className="m-0"
                                    href="/Login"
                                >
                                    <span className="no-icon">Login</span>
                                </Nav.Link>
                            </Nav.Item>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default Header;
