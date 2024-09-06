import axios from "axios";
import authHeader from "./AuthHeader";

const API_URL = "https://carpentryshop-server-production.up.railway.app"

const sendMail = async (to, subject, start, end) => {
    return await axios.get(API_URL + `/api/mail/sendMail?to=${to}&subject=${subject}&start=${start}&end=${end}`, { headers: authHeader() });
}

const AuthMail = {
    sendMail
};

export default AuthMail;