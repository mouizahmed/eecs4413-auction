import { AuctionItem } from '@/types';
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

export const getAvailableItems = async (): Promise<AuctionItem[]> => {
  try {
    const response = await api.get('/auction/get-all');
    return response.data.data.content;
  } catch (error) {
    throw error;
  }
};

export const getAuctionDetails = async (auctionID: string): Promise<AuctionItem> => {
  try {
    const response = await api.get(`/auction/get-by-id?itemID=${auctionID}`);
    return response.data.data.auctionItem;
  } catch (error) {
    throw error;
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

export const getBidHistory = async (itemID: string): Promise<any> => {
  try {
    const response = await api.get(`/auction/${itemID}/bids`);
    return response.data.data.content;
  } catch (error) {
    throw error;
  }
};
