import axios from "axios";
import authHeader from "./AuthHeader";

const API_URL = "https://carpentryshop-server-production.up.railway.app"

const saveFile = async () => {
    return await axios.get(API_URL + '/api/fileBackup/saveFile', { headers: authHeader() });
}

const restoreFile = async (path) => {
    return await axios.get(API_URL + `/api/fileBackup/readFile?path=${path}`, { headers: authHeader() });
}

const getFileBackup = async () => {
    return await axios.get(API_URL + `/api/fileBackup/getFileList`, { headers: authHeader() });
}



const AuthFileBackup = {
    saveFile,
    restoreFile,
    getFileBackup
};

export default AuthFileBackup;