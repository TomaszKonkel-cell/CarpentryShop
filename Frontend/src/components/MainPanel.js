import 'bootstrap-daterangepicker/daterangepicker.css';
import 'bootstrap/dist/css/bootstrap.css';
import React, { useRef, useState } from 'react';

import DateRangePicker from 'react-bootstrap-daterangepicker';

import { cilCloudDownload, cilReload } from '@coreui/icons';
import { CIcon } from '@coreui/icons-react';
import {
    CButton,
    CButtonGroup,
    CCard,
    CCardBody,
    CCardFooter,
    CCol,
    CDropdown,
    CDropdownItem,
    CDropdownMenu,
    CDropdownToggle,
    CRow
} from '@coreui/react';

import { Button } from '@progress/kendo-react-buttons';
import { PDFExport } from '@progress/kendo-react-pdf';
import '@progress/kendo-theme-material/dist/all.css';

import moment from 'moment';
import Swal from 'sweetalert2';
import SalesDocument from '../PDFs/SalesDocument';
import AuthFileBackup from '../service/AuthFileBackup';
import FileBackup from './FileBackup';
import MailForm from './MailForm';
import SalesChart from './SalesChart';


const MainPanel = () => {
    const [range, setRange] = useState(10);
    const [start, setStart] = useState(moment());
    const [end, setEnd] = useState(moment());
    const [display, setDisplay] = useState(true);
    const [mail, setMail] = useState(false);
    const [backup, setBackup] = useState(false);

    const pdfExportComponent = useRef(null);
    const handleExport = () => {
        pdfExportComponent.current.save('myPDF.pdf');
    }
    const handleSave = () => {
        Swal.fire({
            title: "Potwierdzasz zapisanie pliku?",
            showDenyButton: true,
            confirmButtonText: "TAK",
            denyButtonText: `NIE`,
            icon: "question"
        }).then((result) => {

            Swal.fire("Zapisywanie danych do pliku");
            Swal.showLoading();
            if (result.isConfirmed) {
                AuthFileBackup.saveFile().then(
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
        })
    }

    return (

        <CCard style={{ "marginTop": "3%" }}>
            <CCardBody>
                <CRow>
                    <CCol sm={5}>
                        <h4 id="traffic" className="card-title mb-0">
                            Wykres zarobków
                        </h4>
                        <div className="small text-body-secondary">{start.format("YYYY-MM-DD")} - {end.format("YYYY-MM-DD")}</div>
                    </CCol>
                    <CCol sm={7} className="d-none d-md-block">
                        <CDropdown className="float-end">
                            <CDropdownToggle color="secondary"><CIcon icon={cilCloudDownload} /></CDropdownToggle>
                            <CDropdownMenu>
                                <CDropdownItem onClick={handleExport}>Export PDF</CDropdownItem>
                                <CDropdownItem onClick={() => setMail(true)}>Send Mail</CDropdownItem>

                            </CDropdownMenu>
                        </CDropdown>

                        <CButtonGroup className="float-end me-3">
                            <Button onClick={() => setDisplay(!display)} className="float-end">
                                <CIcon icon={cilReload} />
                            </Button>
                        </CButtonGroup>


                        <CButtonGroup className="float-end me-3">
                            <CButton
                                color="secondary"
                                className="mx-0"
                                onClick={() => {
                                    setStart(moment().subtract('weeks').startOf('isoWeek'))
                                    setEnd(moment().subtract('weeks').endOf('isoWeek'))
                                    setRange(0)
                                }}
                            >
                                Tydzień
                            </CButton>
                            <DateRangePicker
                                initialSettings={{ startDate: '7/1/2024', endDate: '7/30/2014' }}
                                onCallback={(start, end) => {
                                    setStart(start)
                                    setEnd(end)
                                    setRange(0)
                                }}
                            >
                                <CButton color="outline-secondary">{start.format("YYYY-MM-DD")}</CButton>
                            </DateRangePicker>
                            <CButton
                                color="secondary"
                                className="mx-0"
                                onClick={() => {
                                    setStart(moment().startOf('month'))
                                    setEnd(moment().endOf('month'))
                                    setRange(0)
                                }}
                            >
                                Miesiąc
                            </CButton>

                        </CButtonGroup>
                    </CCol>
                </CRow>

                <PDFExport ref={pdfExportComponent} >
                    {display ?
                        <SalesChart days={range} startDate={start.format("YYYY-MM-DD")} endDate={end.format("YYYY-MM-DD")} />
                        :
                        <SalesDocument days={range} startDate={start.format("YYYY-MM-DD")} endDate={end.format("YYYY-MM-DD")} />
                    }

                </PDFExport>



            </CCardBody>
            <CCardFooter>
                <CRow className="mb-2">
                    <CButtonGroup className="float-end me-3">
                        <CButton
                            color="outline-secondary"
                            className="mx-0"
                            onClick={handleSave}
                        >
                            Zapisz dane
                        </CButton>

                        <CButton
                            color="outline-secondary"
                            className="mx-0"
                            onClick={() => setBackup(true)}
                        >
                            Przywróć dane
                        </CButton>

                    </CButtonGroup>
                </CRow>
            </CCardFooter>

            <MailForm mail={mail} days={range} startDate={start.format("YYYY-MM-DD")} endDate={end.format("YYYY-MM-DD")} />
            <FileBackup backup={backup} />

        </CCard>

    )
}

export default MainPanel