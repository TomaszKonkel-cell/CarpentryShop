import axios from "axios";
import authHeader from "./AuthHeader";

const API_URL = "https://carpentryshop-server-production.up.railway.app"

const createProject = async (postData) => {
  return await axios.post(API_URL + '/api/projects/add', postData, { headers: authHeader() });
};

const getProjects = async () => {
  return await axios.get(API_URL + '/api/projects/all', { headers: authHeader() });
};

const getProjectsDetails = async (id) => {
  return await axios.get(API_URL + `/api/projects/details?id=${id}`, { headers: authHeader() });
};

const updateProject = async (id, postData) => {
  return await axios.put(API_URL + `/api/projects/update?id=${id}`, postData, { headers: authHeader() });
};

const deleteProject = async (id) => {
  return await axios.delete(API_URL + `/api/projects/delete?id=${id}`, { headers: authHeader() });
};

const restoreProject = async (id) => {
  return await axios.get(API_URL + `/api/projects/restore?id=${id}`, { headers: authHeader() });
};


const AuthProject = {
  createProject,
  getProjects,
  getProjectsDetails,
  updateProject,
  deleteProject,
  restoreProject
};

export default AuthProject;