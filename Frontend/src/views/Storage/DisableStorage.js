import React, { useEffect, useState } from 'react';
import { Button, Container, Table } from 'react-bootstrap';

import AuthStorage from '../../service/AuthStorage';

const DisableStorage = () => {
    const [items, setItems] = useState();
  
    useEffect(() => {
        AuthStorage.getStorage().then((res) => {
            setItems(res.data)
        })
    })
    return (
        <Container >

            <Table striped bordered hover variant="dark">
                <thead>
                    <tr>
                        <th>Nazwa</th>
                        <th>Ilość</th>
                        <th>Opis</th>
                        <th>Kod</th>
                        <th>Typ</th>
                        <th>Kategoria</th>
                        <th>Akcje</th>
                    </tr>
                </thead>

                {items && items.map((item) => {
                    if (item.quantity == 0) {
                        return (
                            <tbody>
                                <tr>
                                    <td>{item.itemName}</td>
                                    <td>{item.quantity}</td>
                                    <td>{item.description}</td>
                                    <td>{item.itemCode}</td>
                                    <td>{item.type}</td>
                                    <td>{item.categories}</td>

                                    <td>
                                        <Button variant="primary" href={`/StorageItemDetails?id=${item.id}`}>Szczegóły</Button>{' '}
                                    </td>

                                </tr>
                            </tbody>
                        );
                    }

                })}

            </Table>
        </Container>



    )
}

export default DisableStorage