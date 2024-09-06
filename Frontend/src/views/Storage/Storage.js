import React, { useState } from 'react';
import { Button, ButtonGroup, Card, Container } from 'react-bootstrap';

import DisableStorage from './DisableStorage';
import EnabledStorage from './EnabledStorage';

const Storage = () => {

    const [view, setView] = useState(true);

    return (
        <Container className="auth-wrapper">
            <Card bg='dark' className="shadow p-3 mb-5 bg-white rounded">
                <Card.Body>
                    <Card.Title style={{ float: "left" }}>Lista Magazynowa</Card.Title>
                    <ButtonGroup style={{ float: "right" }} className="shadow rounded">
                        {view ? (
                            <Button style={{ "marginRight": "3%" }} onClick={() => { setView(!view) }}>Zarchiwizowane</Button>
                        ) : (
                            <Button style={{ "marginRight": "3%" }} onClick={() => { setView(!view) }}>Aktualne</Button>
                        )}
                        <Button href="/CreateStorageItem" style={{ float: "right" }} className="shadow rounded">Nowy</Button>
                    </ButtonGroup>

                </Card.Body>
            </Card>

                    {view ? (
                        <EnabledStorage />
                    ) : (
                        <DisableStorage />
                    )}
                    
        </Container>



    )
}

export default Storage