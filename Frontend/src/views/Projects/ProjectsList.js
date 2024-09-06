import React, { useEffect, useState } from 'react';
import { Button, ButtonGroup, Card, Container, Table } from 'react-bootstrap';

import Swal from 'sweetalert2';
import AuthProject from '../../service/AuthProject';
import AuthUser from '../../service/AuthUser';


const ProjectsList = () => {
  const [projects, setProjects] = useState();
  const [view, setView] = useState(true);

  const currentUser = AuthUser.getCurrentUser();

  useEffect(() => {
    AuthProject.getProjects().then(
      (res) => {
      setProjects(res.data)
    })
  })

  const handleDelete = (id) => {
    Swal.fire({
      title: "Potwierdzasz usunięcie projektu?",
      showDenyButton: true,
      confirmButtonText: "TAK",
      denyButtonText: `NIE`,
      icon: "question"
    }).then((result) => {
      Swal.fire("Usuwanie danych");
      Swal.showLoading();
      if (result.isConfirmed) {
        AuthProject.deleteProject(id).then(
          (res) => {
            Swal.close()
            Swal.fire(res.data, "", "success");
          }, (error) => {
            Swal.close()
            Swal.fire(error.response.data, "", "error");
          })
      } else if (result.isDenied) {
        Swal.fire("Anulowano usuwanie", "", "info");
      }
    })
  }

  const handleRestore = (id) => {
    Swal.fire({
      title: "Potwierdzasz przywrócenie projektu?",
      showDenyButton: true,
      confirmButtonText: "TAK",
      denyButtonText: `NIE`,
      icon: "question"
    }).then((result) => {
      Swal.fire("Przywracanie...")
      Swal.showLoading();
      if (result.isConfirmed) {
        AuthProject.restoreProject(id).then(
          (res) => {
            Swal.close();
            Swal.fire(res.data, "", "success");
          }, (error) => {
            Swal.fire(error.response.data, "", "error");
          })
      } else if (result.isDenied) {
        Swal.fire("Anulowano usuwanie", "", "info");
      }
    })
  }

  return (

    <Container className="auth-wrapper">
      <Card bg='dark' className="shadow p-3 mb-5 bg-white rounded">
        <Card.Body>
          <Card.Title style={{ float: "left" }}>Lista Projektów</Card.Title>
          <ButtonGroup style={{ float: "right" }} className="shadow rounded">
            {view ? (
              <Button style={{ "marginRight": "3%" }} onClick={() => { setView(!view) }}>Zarchiwizowane</Button>
            ) : (
              <Button style={{ "marginRight": "3%" }} onClick={() => { setView(!view) }}>Aktualne</Button>
            )}

            {(currentUser && currentUser.roles.includes("ROLE_ADMIN")) || (currentUser.roles.includes("ROLE_MODERATOR")) ? (
              <Button href="/CreateProject">Nowy</Button>
            ) : (
              <></>
            )}
          </ButtonGroup>


        </Card.Body>
      </Card>
      <Table striped bordered hover variant="dark">
        <thead>
          <tr>
            <th>Nazwa</th>
            <th>Cena</th>
            <th>Opis</th>
            <th>Akcje</th>
          </tr>
        </thead>

        {projects && projects.map((project) => {
          if (project.status == view) {
            return (
              <tbody>
                <tr>
                  <td>{project.name}</td>
                  <td>{project.price}</td>
                  <td>{project.description}</td>
                  {currentUser && currentUser.roles.includes("ROLE_ADMIN") || currentUser.roles.includes("ROLE_MODERATOR") ? (
                    <td>
                      <Button variant="primary" href={`/ProjectDetails?id=${project.id}`}>Szczegóły</Button>{' '}
                      {view ? (
                        <Button variant="danger" onClick={() => handleDelete(project.id)}>Usuń</Button>
                      ) : (
                        <Button variant="success" onClick={() => handleRestore(project.id)}>Przywróć</Button>
                      )}

                    </td>
                  ) : (
                    <td>
                      <Button variant="success" href={`/ProjectDetails?id=${project.id}`}>Szczegóły</Button>
                    </td>
                  )
                  }

                </tr>
              </tbody>
            );
          }

        })}

      </Table>
    </Container>

  )
}

export default ProjectsList