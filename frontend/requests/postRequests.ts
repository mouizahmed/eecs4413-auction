import { ForgotPasswordData, RegisterData } from '@/types';
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

export const placeBid = async (itemID: string, bidAmount: number): Promise<any> => {
  try {
    const response = await api.post('/auction/bid', null, {
      params: {
        itemID,
        bidAmount: bidAmount.toString(),
      },
    });
    return response.data.data;
  } catch (error) {
    throw error;
  }
};
