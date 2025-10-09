import axios from 'axios';

const API_BASE_URL = '/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

api.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('API Error:', {
      message: error.message,
      status: error.response?.status,
      url: error.config?.url
    });
    
    if (error.response?.status === 404) {
      error.message = 'Resource not found';
    } else if (error.response?.status === 500) {
      error.message = 'Server error occurred';
    } else if (error.code === 'ECONNABORTED') {
      error.message = 'Request timeout';
    } else if (!error.response) {
      error.message = 'Network error - please check your connection';
    }
    
    return Promise.reject(error);
  }
);

export const clientAPI = {
  createClient: (clientData) => api.post('/clients', clientData),
  
  getClients: (page = 0, size = 10, sortBy = 'firstName', direction = 'asc') => 
    api.get(`/clients?page=${page}&size=${size}&sortBy=${sortBy}&direction=${direction}`),
  
  getClientByOib: (oib) => api.get(`/clients/${oib}`),
  
  deleteClient: (oib) => api.delete(`/clients/${oib}`),
  
  updateStatus: (oib, status) => api.patch(`/clients/${oib}/status`, { status }),
  
  createCardRequest: (cardRequest) => api.post('/card-requests', cardRequest),
};

export const testConnection = async () => {
  try {
    const response = await api.get('/clients?page=0&size=1');
    return response.status === 200;
  } catch (error) {
    console.error('Backend connection test failed:', error.message);
    return false;
  }
};

export default api;