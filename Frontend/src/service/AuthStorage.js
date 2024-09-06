import axios from "axios";
import authHeader from "./AuthHeader";

const API_URL = "https://carpentryshop-server-production.up.railway.app"

const createStorageItem = async (postData) => {
  return await axios.post(API_URL + '/api/storage/add', postData, { headers: authHeader() });
};

const getStorage = async () => {
  return await axios.get(API_URL + '/api/storage/all', { headers: authHeader() });
};

const getStorageItemDetails = async (id) => {
  return await axios.get(API_URL + `/api/storage/details?id=${id}`, { headers: authHeader() });
};

const updateStorageItem = async (id, postData) => {
  return await axios.put(API_URL + `/api/storage/update?id=${id}`, postData, { headers: authHeader() });
};

const deleteStorageItem = async (id) => {
  return await axios.delete(API_URL + `/api/storage/delete?id=${id}`, { headers: authHeader() });
};


const AuthStorage = {
  createStorageItem,
  getStorage,
  getStorageItemDetails,
  updateStorageItem,
  deleteStorageItem
};

export default AuthStorage;