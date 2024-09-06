import React from 'react';

import AuthUser from "../service/AuthUser";
import LoggedRequired from './LoggedRequired';

import { Container } from 'react-bootstrap';
import MainPanel from '../components/MainPanel';
import StatsWidget from '../components/StatsWidget';

const Home = () => {
  const currentUser = AuthUser.getCurrentUser();


  return (

    <Container style={{ "marginTop": "3%" }}>
      {currentUser ? (
        
        <>
          <StatsWidget />
          <MainPanel />
        </>

      ) : (
        <LoggedRequired />
      )}
    </Container>

  )
}

export default Home