import axios from "axios";
import authHeader from "./AuthHeader";

const API_URL = "https://carpentryshop-server-production.up.railway.app"

const getTodayEarnings = async () => {
    return await axios.get(API_URL + '/api/stats/todayEarnings', { headers: authHeader() });
}

const earningsOfRange = async (days, startDate, endDate) => {
    return await axios.get(API_URL + `/api/stats/earningsOfRange?days=${days}&startDate=${startDate}&endDate=${endDate}`, { headers: authHeader() });
}

const getSumOfProjects = async () => {
    return await axios.get(API_URL + '/api/stats/sumOfProjects', { headers: authHeader() });
}

const sumOfProjectsRange = async (days) => {
    return await axios.get(API_URL + `/api/stats/sumOfProjectsRange?days=${days}`, { headers: authHeader() });
}

const getPercentageDiff = async () => {
    return await axios.get(API_URL + '/api/stats/percentageCompare', { headers: authHeader() });
}




const AuthJobs = {
    getTodayEarnings,
    earningsOfRange,
    getSumOfProjects,
    sumOfProjectsRange,
    getPercentageDiff,
};

export default AuthJobs;