import '@progress/kendo-theme-material/dist/all.css';
import 'bootstrap-daterangepicker/daterangepicker.css';
import 'bootstrap/dist/css/bootstrap.css';

import { Button } from '@progress/kendo-react-buttons';
import React, { useEffect, useState } from 'react';
import { Form, Modal } from 'react-bootstrap';

import Swal from 'sweetalert2';
import AuthFileBackup from '../service/AuthFileBackup';
import { CCol, CRow } from '@coreui/react';

const FileBackup = (props) => {
  const [filesBackup, setFilesBackup] = useState();
  const [path, setPath] = useState();

  useEffect(() => {
    AuthFileBackup.getFileBackup().then((res) => {
      setFilesBackup(res.data)
    })
  }, [])

  const handleCheck = (value) => {
    setPath(value);
  }

  const handleBackupRestore = () => {
    Swal.fire({
      title: "Czy chcesz przywrócić projekty z pliku zapasowego?",
      showDenyButton: true,
      confirmButtonText: "TAK",
      denyButtonText: `NIE`,
      showLoaderOnConfirm: true,
      icon: "question"
    }).then((result) => {
      if (result.isConfirmed) {
        Swal.fire("Wczytywanie danych z pliku");
        Swal.showLoading();

        AuthFileBackup.restoreFile(path).then(
          (res) => {
            Swal.close()
            let names = null;

            if (res.data.length == 0) {
              names = "Already Up To Date"
            } else {
              names = res.data.map((x, i) => {
              return `<li>${x}</li>`
            }).join('')
            }
            

            Swal.fire({
              title: "Przywrócono z pliku :" + "\n",
              html: `<ol>` + names + `<ol>`,
              icon: "success"
            });

          }, (error) => {
            Swal.close()
            Swal.fire(error.response.data, "", "error");
          })
      } else if (result.isDenied) {
        Swal.fire("Przywracanie z pliku anulowane", "", "info");
      }
    })

  }
  const projectsFiles = filesBackup && filesBackup.filter((x) => x.includes("Projects"));
  const storagesFiles = filesBackup && filesBackup.filter((x) => x.includes("Storages"));

  return (

    <Modal show={props.backup} onHide={() => window.location.reload()} size="lg">
      <Modal.Header closeButton>
        <Modal.Title>Dostępne pliki do przywrócenia</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <CRow>
          <CCol style={{ "textAlign": "center", "borderRight": "1px solid black" }}>
            {projectsFiles && projectsFiles.map((x) =>
              <Form.Check style={{ "textAlign": "left" }}
                type="radio"
                name="radioInput"
                id={x}
                label={x}
                onChange={() => { handleCheck(x) }}
              />)}
          </CCol>
          <CCol style={{ "textAlign": "center" }}>
            {storagesFiles && storagesFiles.map((x) =>
              <Form.Check style={{ "textAlign": "left" }}
                type="radio"
                name="radioInput"
                id={x}
                label={x}
                onChange={() => { handleCheck(x) }}
              />)}

          </CCol>
        </CRow>


      </Modal.Body>
      <Modal.Footer>
        <Button variant="primary" onClick={() => window.location.reload()}>
          Wróc
        </Button>
        <Button variant="success" onClick={() => { handleBackupRestore() }}>
          Przywróć
        </Button>
      </Modal.Footer>
    </Modal>

  )
}

export default FileBackup