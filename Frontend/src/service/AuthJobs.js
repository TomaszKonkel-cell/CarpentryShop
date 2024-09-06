import axios from "axios";
import authHeader from "./AuthHeader";

const API_URL = "https://carpentryshop-server-production.up.railway.app"

const getCurrentJobs = async () => {
    return await axios.get(API_URL + '/api/order/all', { headers: authHeader() });
}

const closeOrder = async (id) => {
    return await axios.put(API_URL + `/api/order/end?projectsListId=${id}`, null, { headers: authHeader() });
}

const getJobDetails = async (id) => {
    return await axios.get(API_URL + `/api/resources/details?id=${id}`, { headers: authHeader() });
};


const addResources = async (id) => {
    const resources = JSON.parse(localStorage.getItem("resources"));
    const resourcesForJob = resources.find(item => item.id == id);
    return await axios.post(API_URL + `/api/resources/add?id=${id}`, resourcesForJob.resources, { headers: authHeader() });
}


const deleteResourcesFromDatabase = async (job) => {
    return await axios.delete(API_URL + `/api/resources/deleteResources?projectsListId=${job}`, { headers: authHeader() });
}


const getResourcesForJob = (id) => {
    const resources = JSON.parse(localStorage.getItem("resources"));
    return resources?.filter(item => item.id == id)
}

const addResourceToLocal = async (resource) => {
    const resources = JSON.parse(localStorage.getItem("resources")) || [];
    if (resources.length === 0) {
        createNewResources(resource)
        return "Dodano pierwsze zasoby";
    }
    if (!findDuplicate(resource, resources)) {
        const newResource =
        {
            "id": resource.id,
            resources: [{
                "quantity": resource.quantity,
                "item": resource.item
            }]
        }

        const newResources = [...resources, newResource]
        localStorage.setItem("resources", JSON.stringify(newResources))
        return "Dodano zasoby do nowego zlecenia";
    }
    return "Dodano do obecnego zlecenia"

}


const createNewResources = (resource) => {
    const newResource = [
        {
            "id": resource.id,
            resources: [{
                "quantity": resource.quantity,
                "item": resource.item
            }]
        }
    ];
    localStorage.setItem("resources", JSON.stringify(newResource))
}

const findDuplicate = (resource, resources) => {
    var duplicate = false;
    var result = false;
    resources.forEach(job => {
        if (job.id === resource.id) {
            result = true;
            job.resources.forEach(storage => {
                if (storage.item.id == resource.item.id) {
                    duplicate = true;
                    storage.quantity += resource.quantity
                    localStorage.setItem("resources", JSON.stringify(resources))
                }
            })
            if (!duplicate) {
                job.resources.push({
                    "quantity": resource.quantity,
                    "item": resource.item
                })
                localStorage.setItem("resources", JSON.stringify(resources))
            }
        }
    });
    return result;
}

const deleteResource = async (idJob, itemName) => {
    const resources = JSON.parse(localStorage.getItem("resources"));
    const resourcesForJob = resources.find(item => item.id == idJob);
    const newResources = [];

    resourcesForJob.resources.forEach((element) => {
        if (element.item.itemName !== itemName) {
            newResources.push(element)
        }
    })
    resourcesForJob.resources = newResources
    localStorage.setItem("resources", JSON.stringify(resources))
    return "Usunięto zasób"

}

const deleteFromLocal = async (id) => {
    const resources = JSON.parse(localStorage.getItem("resources"));
    const newResources = resources.filter(item => item.id != id);
    localStorage.setItem("resources", JSON.stringify(newResources))
}

const restoreToLocal = async (job) => {
    const resources = JSON.parse(localStorage.getItem("resources")) || [];
    const list = []
    job.resources.forEach((resource) => {
        const temp = {
            "quantity": resource.quantity,
            "item": resource.item
        }
        list.push(temp)
    })

    const newResource =
    {
        "id": job.id,
        resources: list
    }

    const newResources = [...resources, newResource]
    localStorage.setItem("resources", JSON.stringify(newResources))
}



const AuthJobs = {
    getCurrentJobs,
    getJobDetails,
    addResources,
    closeOrder,
    deleteResource,
    deleteFromLocal,
    deleteResourcesFromDatabase,
    restoreToLocal,
    addResourceToLocal,
    getResourcesForJob
};

export default AuthJobs;