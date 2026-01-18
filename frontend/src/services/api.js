import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('jwt');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (username, password) => api.post('/auth/loginb', { username, password }),
  register: (username, password, email, department) => api.post('/auth/registerb', { username, password, email, department }),
  getDepartments: () => api.get('/auth/departments'),
};

// Admin API
export const adminAPI = {
  getUsers: () => api.get('/admin/getusers'),
  getUserByName: (uname) => api.get(`/admin/getuser/${uname}`),
  deleteUser: (uname) => api.delete(`/admin/delete/${uname}`),
  updateUser: (uname, userData) => api.put(`/admin/update/${uname}`, userData),
  getDepartments: () => api.get('/admin/getdepartments'),
};

// RBAC API
export const rbacAPI = {
  getEmployeeRecords: () => api.get('/getemployeerecords'),
  getCustomerRecords: () => api.get('/getcustomerrecords'),
  getEmailRecords: () => api.get('/getemailrecords'),
  getTimeCards: () => api.get('/timecards'),
};

export default api;
