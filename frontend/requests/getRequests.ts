import { AuctionItem, ReceiptResponseDTO } from '@/types';
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
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to get current user');
    }
    throw new Error('Failed to get current user');
  }
};

export const getUnpaidItems = async (): Promise<AuctionItem[]> => {
  try {
    const response = await api.get('/user/unpaid-items');
    return response.data.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to get unpaid items for user');
    }
    throw new Error('Failed to get unpaid items for user');
  }
};

export const getSecurityQuestion = async (username: string): Promise<any> => {
  try {
    const response = await api.get(`/user/get-security-question?username=${username}`);
    return response.data.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to get security question');
    }
    throw new Error('Failed to get security question');
  }
};

export const getAvailableItems = async (): Promise<AuctionItem[]> => {
  try {
    const response = await api.get('/auction/get-all');
    return response.data.data.content;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to get available items');
    }
    throw new Error('Failed to get available items');
  }
};

export const getAuctionDetails = async (auctionID: string): Promise<AuctionItem> => {
  try {
    const response = await api.get(`/auction/get-by-id?itemID=${auctionID}`);
    return response.data.data.auctionItem;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to get auction details');
    }
    throw new Error('Failed to get auction details');
  }
};

export const getBidHistory = async (itemID: string): Promise<any> => {
  try {
    const response = await api.get(`/auction/bids?itemID=${itemID}`);
    return response.data.data.content;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to get bidding history');
    }
    throw new Error('Failed to get bidding history');
  }
};

export const getReceiptDetails = async (receiptID: string): Promise<ReceiptResponseDTO> => {
  try {
    const response = await api.get(`/auction/receipt?receiptID=${receiptID}`);
    console.log(response.data.data.receipt);
    return response.data.data.receipt;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to fetch receipt details');
    }
    throw new Error('Failed to fetch receipt details');
  }
};

export const getUserReceipts = async (): Promise<ReceiptResponseDTO[]> => {
  try {
    const response = await api.get('/auction/receipts');
    return response.data.data.map((item: any) => item.receipt);
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to fetch user receipts');
    }
    throw new Error('Failed to fetch user receipts');
  }
};

export const getWonAuctions = async (): Promise<AuctionItem[]> => {
  try {
    const response = await api.get('/auction/won');
    return response.data.data.content.map((item: any) => item.auctionItem);
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to fetch won auctions');
    }
    throw new Error('Failed to fetch won auctions');
  }
};
