import React, { useEffect, useState } from 'react';
import { Button, Card, Container, Table } from 'react-bootstrap';

import AuthUser from "../../service/AuthUser";
import Swal from 'sweetalert2';

const UsersList = () => {
  const [users, setUsers] = useState();

  const deleteUser = (id) => {
    Swal.fire({
      title: "Potwierdzasz usunięcie użytkownika?",
      showDenyButton: true,
      confirmButtonText: "TAK",
      denyButtonText: `NIE`,
      icon: "question"
    }).then((result) => {
      Swal.fire("Usuwanie...");
      Swal.showLoading();
      if (result.isConfirmed) {
        AuthUser.deleteUser(id).then(
          (res) => {
            Swal.close()
            AuthUser.getUsers().then((res) => {
              setUsers(res.data)
            })
            Swal.fire(res.data, "", "success");
          },
          (error) => {
            Swal.fire(error.data.message, "", "danger");
          }
        );
      } else if (result.isDenied) {
        Swal.fire("Anulowano zmienianie", "", "info");
      }
    })
  };

  useEffect(() => {
    AuthUser.getUsers().then((res) => {
      setUsers(res.data)
    })
  }, [])

  return (

    <Container className='auth-wrapper'>
      <Container>
        <Card bg='dark' className="shadow p-3 mb-5 bg-white rounded">
          <Card.Body>
            <Card.Title style={{ float: "left" }}>Lista Userów</Card.Title>
            <Button href="/CreateUser" style={{ float: "right" }}>Nowy</Button>
          </Card.Body>
        </Card>

        <Table striped bordered hover variant="dark">
          <thead>
            <tr>
              <th>Nazwa</th>
              <th>Uprawnienia</th>
              <th>Akcje</th>
            </tr>
          </thead>

          {users && users.map((user) => {
            if (user.username !== "JestemAdmin") {
              return (
                <tbody>
                  <tr>
                    <td>{user.username}</td>
                    <td>
                      {user.roles && user.roles.map((role) => {
                        return (
                          <p>{role.name}</p>
                        )
                      })}
                    </td>

                    <td>
                      <Button variant="primary" href={`/UserDetails?id=${user.id}`}>Szczegóły</Button>{' '}
                      <Button variant="danger"
                        onClick={() => deleteUser(user.id)}>Usuń</Button>{' '}
                    </td>
                  </tr>
                </tbody>
              );
            }

          })}

        </Table>
      </Container>
    </Container>

  )
}

export default UsersList