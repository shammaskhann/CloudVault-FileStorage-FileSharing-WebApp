import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth APIs
export const authAPI = {
  register: async (userData) => {
    try {
      const response = await api.post('/api/auth/register', userData);
      console.log(response);  
      return response.data;
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 'Registration failed. Please try again.'
      );
    }
  },

  login: async (credentials) => {
    try {
      const response = await api.post('/api/auth/login', credentials);
      return response.data;
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 'Login failed. Please check your credentials.'
      );
    }
  },
};

// File APIs
export const fileAPI = {
  getMyFiles: async () => {
    try {
      const response = await api.get('/api/files/my');
      return response.data;
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 'Failed to fetch files. Please try again.'
      );
    }
  },

  uploadFile: async (file) => {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await api.post('/api/s3/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      console.log(response);  
      return response.data;
    } catch (error) {
      console.log(error);  
      throw new Error(
        error.response?.data?.message || 'File upload failed. Please try again.'
      );
    }
  },

  downloadFile: async (filename) => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`${API_BASE_URL}/api/s3/download/${filename}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        responseType: 'blob',
      });
      return response.data;
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 'File download failed. Please try again.'
      );
    }
  },

  deleteFile: async (fileId) => {
    try {
      const response = await api.delete(`/api/s3/delete/${fileId}`);
      return response.data;
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 'File deletion failed. Please try again.'
      );
    }
  },

  viewFile: async (fileUrl) => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(fileUrl, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        responseType: 'blob',
      });
      return response.data;
    } catch (error) {
      throw new Error(
        error.response?.data?.message || 'Failed to view file. Please try again.'
      );
    }
  },
};

export default api;

