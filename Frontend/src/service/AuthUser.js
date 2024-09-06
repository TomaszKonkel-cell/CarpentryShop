import axios from "axios";
import authHeader from "./AuthHeader";
const API_URL = "https://carpentryshop-server-production.up.railway.app"

const register = async (postData) => {
  return await axios.post(API_URL + '/api/users/signup', postData, { headers: authHeader() });  
};

const login = async (postData) => {
  const response = await axios.post(API_URL +'/api/users/signin', postData);

    if (response.data.token) {
      localStorage.setItem("user", JSON.stringify(response.data))
    }
 
};

const getUsers = async () => {
  return await axios.get(API_URL + '/api/users/get', { headers: authHeader() });  
};
 
const getUsersDetails = async (id) => {
  return await axios.get(API_URL + `/api/users/details?id=${id}`, { headers: authHeader() });  
};

const deleteUser = async (id) => {
  return await axios.delete(API_URL + `/api/users/delete?id=${id}`, { headers: authHeader() });  
};

const deleteRole = async (id, role) => {
  return await axios.delete(API_URL + `/api/users/deleteRole?id=${id}&role=${role}`, { headers: authHeader() });  
};

const changeUsername = async (id, username) => {
  return await axios.put(API_URL + `/api/users/changeUsername?username=${username}&id=${id}`, null, { headers: authHeader() });  
};

const changePassword = async (oldPass, newPass, id) => {
  return await axios.put(API_URL +`/api/users/changePassword?oldPass=${oldPass}&newPass=${newPass}&id=${id}`, null, { headers: authHeader() });  
};

const logout = () => {
  localStorage.removeItem("user");
};

const getCurrentUser = () => {
  return JSON.parse(localStorage.getItem("user"));
};

const AuthService = {
  register,
  login,
  logout,
  getCurrentUser,
  getUsers,
  getUsersDetails,
  deleteUser,
  deleteRole,
  changeUsername,
  changePassword
};

export default AuthService;