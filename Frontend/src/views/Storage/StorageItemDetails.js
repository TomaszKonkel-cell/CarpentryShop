import React, { useEffect, useState } from 'react';
import { Button, Card, Container, Form } from 'react-bootstrap';
import { useLocation, useNavigate } from "react-router-dom";

import AuthStorage from '../../service/AuthStorage';

const ProjectDetails = () => {
    const useQuery = () => {
        return new URLSearchParams(useLocation().search);
    };

    let query = useQuery();
    let navigate = useNavigate();
    const [item, setItem] = useState('');
    const [message, setMessage] = useState('');
    const [messageList, setMessageList] = useState({});


    const handleChange = (e) => {
        console.log(item)
        setItem({ ...item, [e.target.name]: e.target.value });
    };

    const handlePut = (e) => {
        const postData = new FormData();
        postData.append('itemName', item.itemName);
        postData.append('quantity', item.quantity);
        postData.append('description', item.description)
        postData.append('type', item.type)
        postData.append('categories', item.categories)

        AuthStorage.updateStorageItem(item.id, postData).then(
            () => {
                navigate('/Storage')
            },
            (error) => {
                if (error.response.status === 401) {
                    setMessage(error.response.data.message)
                } else {
                    if (typeof error.response.data == "string") {
                        setMessage(error.response.data)
                    }
                    if (typeof error.response.data == "object") {
                        setMessageList(error.response.data)
                    }
                }

            }
        );
    };

    useEffect(() => {
        AuthStorage.getStorageItemDetails(query.get("id")).then(
            (res) => {
                setItem(res.data)
            });
    }, [])


    return (
        <Container className='auth-wrapper'>
            <Card bg='dark' className="shadow p-3 mb-5 bg-white rounded">
                <Form >

                    <Form.Group>
                        <Form.Label>Nazwa Przedemiotu</Form.Label>
                        <Form.Control
                            type="text"
                            name="itemName"
                            placeholder="Wprowadź nazwe"
                            onChange={handleChange}
                            defaultValue={item.itemName}
                        />

                    </Form.Group>

                    <Form.Group>
                        <Form.Label>Ilość</Form.Label>
                        <Form.Control
                            type="number"
                            name="quantity"
                            placeholder="Wprowadź ilość"
                            onChange={handleChange}
                            defaultValue={item.quantity}
                        />

                    </Form.Group>

                    <Form.Group>
                        <Form.Label>Opis</Form.Label>
                        <Form.Control
                            as="textarea"
                            name="description"
                            placeholder="Wprowadź opis"
                            onChange={handleChange}
                            defaultValue={item.description}
                        />

                    </Form.Group>

                    <Form.Group >
                        <Form.Label>Typ przedmiotu</Form.Label>
                        <Form.Control as='select'
                            name="type"
                            onChange={handleChange} >
                            <option>{item.type}</option>
                            <option value="LIQUID">Płynny</option>
                            <option value="CONSTANT">Stały</option>
                        </Form.Control>

                    </Form.Group>

                    <Form.Group >
                        <Form.Label>Kategoria przedmiotu</Form.Label>
                        <Form.Control as='select'
                            name="categories"
                            defaultValue={item.categories}
                            onChange={handleChange} >
                            <option>{item.categories}</option>
                            <option value="WOOD">Drewniany</option>
                            <option value="METAL">Metalowy</option>
                            <option value="PAINT">Farba</option>
                        </Form.Control>

                    </Form.Group>


                    {message && (
                        <div className="form-group">
                            <div className="alert alert-danger" role="alert">
                                {message}
                            </div>
                        </div>
                    )}


                    {!message && messageList.errors && Object.values(messageList.errors).map((mes, i) => {
                        return (
                            <div className="form-group">
                                <div className="alert alert-danger" role="alert">
                                    {mes}
                                </div>
                            </div>
                        );
                    })}

                    <div className="button">
                        <Button style={{ "float": 'right' }} onClick={handlePut}>Wprowadź zmiany</Button>
                    </div>
                </Form>
            </Card>
        </Container>



    )
}

export default ProjectDetails