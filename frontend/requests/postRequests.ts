import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
});

export const signIn = async (username: string, password: string): Promise<any> => {
  try {
    const response = await api.post('/user/sign-in', { username, password });
    return response.data.data;
  } catch (error: unknown) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to sign in');
    }
    throw new Error('Failed to sign in');
  }
};

export const signOut = async (): Promise<void> => {
  try {
    await api.post('/user/sign-out');
  } catch (error: unknown) {
    console.log('Error during sign-out:', error);
  }
};

// Based on the backend SignUpDTO requirements
export interface RegisterData {
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  streetName: string;
  streetNum: number;
  postalCode: string;
  city: string;
  country: string;
  securityQuestion: string;
  securityAnswer: string;
}

export interface ForgotPasswordData {
  username: string;
  newPassword: string;
  securityAnswer: string;
}

export const register = async (userData: RegisterData): Promise<any> => {
  try {
    const response = await api.post('/user/sign-up', userData);
    return response.data.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message);
    }
    throw new Error('Failed to register');
  }
};

export const forgotPassword = async (forgotPasswordData: ForgotPasswordData): Promise<any> => {
  try {
    const response = await api.post(`/user/forgot-password?username=${forgotPasswordData.username}`, {
      newPassword: forgotPasswordData.newPassword,
      securityAnswer: forgotPasswordData.securityAnswer,
    });
    return response.data.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message);
    }
    throw new Error('Failed to reset password.');
  }
};
