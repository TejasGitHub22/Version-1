// API Configuration
const API_BASE_URL = 'http://localhost:8080';

// Auth token management
const getAuthToken = () => localStorage.getItem('authToken');
const setAuthToken = (token) => localStorage.setItem('authToken', token);
const removeAuthToken = () => localStorage.removeItem('authToken');

// API request helper
const apiRequest = async (url, options = {}) => {
    const token = getAuthToken();
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers,
    };

    try {
        const response = await fetch(`${API_BASE_URL}${url}`, config);
        
        if (response.status === 401) {
            removeAuthToken();
            window.location.href = '/login';
            return null;
        }

        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || `HTTP error! status: ${response.status}`);
        }

        return data;
    } catch (error) {
        console.error('API request failed:', error);
        throw error;
    }
};

// Authentication API
export const authAPI = {
    login: async (credentials) => {
        const data = await apiRequest('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify(credentials),
        });
        
        if (data && data.jwt) {
            setAuthToken(data.jwt);
        }
        
        return data;
    },

    signup: async (userData) => {
        return await apiRequest('/api/auth/signup', {
            method: 'POST',
            body: JSON.stringify(userData),
        });
    },

    logout: () => {
        removeAuthToken();
    },

    isAuthenticated: () => {
        return !!getAuthToken();
    }
};

// Health API
export const healthAPI = {
    check: async () => {
        return await apiRequest('/api/health');
    },

    ping: async () => {
        return await apiRequest('/api/health/ping');
    }
};

// Users API
export const usersAPI = {
    getAll: async () => {
        return await apiRequest('/api/users');
    },

    getById: async (id) => {
        return await apiRequest(`/api/users/${id}`);
    },

    getByRole: async (role) => {
        return await apiRequest(`/api/users/role/${role}`);
    },

    create: async (userData) => {
        return await apiRequest('/api/users', {
            method: 'POST',
            body: JSON.stringify(userData),
        });
    },

    update: async (id, userData) => {
        return await apiRequest(`/api/users/${id}`, {
            method: 'PUT',
            body: JSON.stringify(userData),
        });
    },

    delete: async (id) => {
        return await apiRequest(`/api/users/${id}`, {
            method: 'DELETE',
        });
    },

    reactivate: async (id) => {
        return await apiRequest(`/api/users/${id}/reactivate`, {
            method: 'POST',
        });
    }
};

// Facilities API
export const facilitiesAPI = {
    getAll: async () => {
        return await apiRequest('/api/facilities');
    },

    getById: async (id) => {
        return await apiRequest(`/api/facilities/${id}`);
    },

    getByLocation: async (location) => {
        return await apiRequest(`/api/facilities/location/${location}`);
    },

    getWithMachines: async (id) => {
        return await apiRequest(`/api/facilities/${id}/with-machines`);
    },

    create: async (facilityData) => {
        return await apiRequest('/api/facilities', {
            method: 'POST',
            body: JSON.stringify(facilityData),
        });
    },

    update: async (id, facilityData) => {
        return await apiRequest(`/api/facilities/${id}`, {
            method: 'PUT',
            body: JSON.stringify(facilityData),
        });
    },

    delete: async (id) => {
        return await apiRequest(`/api/facilities/${id}`, {
            method: 'DELETE',
        });
    },

    reactivate: async (id) => {
        return await apiRequest(`/api/facilities/${id}/reactivate`, {
            method: 'POST',
        });
    }
};

// Coffee Machines API
export const machinesAPI = {
    getAll: async () => {
        return await apiRequest('/api/machines');
    },

    getById: async (id) => {
        return await apiRequest(`/api/machines/${id}`);
    },

    getByFacility: async (facilityId) => {
        return await apiRequest(`/api/machines/facility/${facilityId}`);
    },

    getByStatus: async (status) => {
        return await apiRequest(`/api/machines/status/${status}`);
    },

    getOperational: async () => {
        return await apiRequest('/api/machines/operational');
    },

    getLowSupplies: async () => {
        return await apiRequest('/api/machines/low-supplies');
    },

    create: async (machineData) => {
        return await apiRequest('/api/machines', {
            method: 'POST',
            body: JSON.stringify(machineData),
        });
    },

    update: async (id, machineData) => {
        return await apiRequest(`/api/machines/${id}`, {
            method: 'PUT',
            body: JSON.stringify(machineData),
        });
    },

    updateLevels: async (id, levels) => {
        const params = new URLSearchParams(levels);
        return await apiRequest(`/api/machines/${id}/levels?${params}`, {
            method: 'POST',
        });
    },

    updateStatus: async (id, status) => {
        return await apiRequest(`/api/machines/${id}/status?status=${status}`, {
            method: 'POST',
        });
    },

    brew: async (brewCommand) => {
        return await apiRequest('/api/machines/brew', {
            method: 'POST',
            body: JSON.stringify(brewCommand),
        });
    },

    delete: async (id) => {
        return await apiRequest(`/api/machines/${id}`, {
            method: 'DELETE',
        });
    }
};

// Alerts API
export const alertsAPI = {
    getAll: async () => {
        return await apiRequest('/api/alerts');
    },

    getById: async (id) => {
        return await apiRequest(`/api/alerts/${id}`);
    },

    getByMachine: async (machineId) => {
        return await apiRequest(`/api/alerts/machine/${machineId}`);
    },

    getByType: async (alertType) => {
        return await apiRequest(`/api/alerts/type/${alertType}`);
    },

    getCritical: async () => {
        return await apiRequest('/api/alerts/critical');
    },

    getSupply: async () => {
        return await apiRequest('/api/alerts/supply');
    },

    getRecent: async (hours = 24) => {
        return await apiRequest(`/api/alerts/recent?hours=${hours}`);
    },

    getToday: async () => {
        return await apiRequest('/api/alerts/today');
    },

    create: async (alertData) => {
        return await apiRequest('/api/alerts', {
            method: 'POST',
            body: JSON.stringify(alertData),
        });
    },

    delete: async (id) => {
        return await apiRequest(`/api/alerts/${id}`, {
            method: 'DELETE',
        });
    }
};

// Usage History API
export const usageAPI = {
    getAll: async () => {
        return await apiRequest('/api/usage');
    },

    getById: async (id) => {
        return await apiRequest(`/api/usage/${id}`);
    },

    getByMachine: async (machineId) => {
        return await apiRequest(`/api/usage/machine/${machineId}`);
    },

    getByUser: async (userId) => {
        return await apiRequest(`/api/usage/user/${userId}`);
    },

    getToday: async () => {
        return await apiRequest('/api/usage/today');
    },

    getStatistics: async () => {
        return await apiRequest('/api/usage/statistics');
    },

    create: async (usageData) => {
        return await apiRequest('/api/usage', {
            method: 'POST',
            body: JSON.stringify(usageData),
        });
    }
};

// Usage History API
export const usageHistoryAPI = {
    getAll: async () => {
        return await apiRequest('/api/usage-history');
    },

    getByMachine: async (machineId) => {
        return await apiRequest(`/api/usage-history/machine/${machineId}`);
    },

    getByUser: async (userId) => {
        return await apiRequest(`/api/usage-history/user/${userId}`);
    },

    getToday: async () => {
        return await apiRequest('/api/usage-history/today');
    },

    getStatistics: async () => {
        return await apiRequest('/api/usage-history/statistics');
    }
};

// Dashboard API (if implemented)
export const dashboardAPI = {
    getSummary: async () => {
        return await apiRequest('/api/dashboard/summary');
    },

    getFacilityDashboard: async (facilityId) => {
        return await apiRequest(`/api/dashboard/facility/${facilityId}`);
    },

    getMachineDashboard: async (machineId) => {
        return await apiRequest(`/api/dashboard/machine/${machineId}`);
    }
};

export default {
    authAPI,
    healthAPI,
    usersAPI,
    facilitiesAPI,
    machinesAPI,
    alertsAPI,
    usageAPI,
    usageHistoryAPI,
    dashboardAPI
};