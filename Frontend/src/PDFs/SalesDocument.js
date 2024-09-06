import React, { useEffect, useState } from 'react';
import { Container, Table } from "react-bootstrap";
import AuthStats from '../service/AuthStats';


const SalesDocument = (props) => {
    const [earningData, setEarningData] = useState(Object);
    useEffect(() => {
        AuthStats.earningsOfRange(props.days, props.startDate, props.endDate).then(
            (res) => {
                setEarningData(res.data)
            });
    })

    const total = Object.values(earningData).reduce((x, sum) => {
        sum += x;
        return sum;
    }, 0)

    return (
        <Container>
            <Table striped bordered hover variant="dark">
                <thead>
                    <tr>
                        <th>Dzień miesiąca</th>
                        <th>Utarg</th>
                    </tr>
                </thead>

                {Object.keys(earningData).map((x) => {
                    return (
                        <tbody>
                            <tr>
                                <td>{x}</td>
                                <td>{earningData[x]}</td>
                            </tr>
                        </tbody>
                    );
                })}
                <tfoot>
                    <tr>
                        <td colSpan="4" style={{ "textAlign": "right" }}>
                           Suma: {total}
                        </td>

                    </tr>
                </tfoot>
            </Table>
        </Container>
    );
}

export default SalesDocument;
