import axios, { AxiosError } from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
});

export const getCurrentUser = async (): Promise<any> => {
  try {
    const response = await api.get('/user/current');
    return response.data.data;
  } catch (error) {
    console.log(error);
  }
};

export const getSecurityQuestion = async (username: string): Promise<any> => {
  try {
    const response = await api.get(`/user/get-security-question?username=${username}`);
    return response.data.data;
  } catch (error) {
    throw error;
  }
};
