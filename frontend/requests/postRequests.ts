import {
  AuctionForm,
  AuctionItem,
  AuctionType,
  ForgotPasswordData,
  PaymentData,
  PaymentResponseDTO,
  RegisterData,
  RatingRequestDTO,
} from '@/types';
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
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to sign out');
    }
    throw new Error('Failed to sign out');
  }
};

export const register = async (userData: RegisterData): Promise<any> => {
  try {
    const response = await api.post('/user/sign-up', userData);
    return response.data.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to register');
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
      throw new Error(error.response.data.message || 'Failed to reset password');
    }
    throw new Error('Failed to reset password');
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
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to place bid');
    }
    throw new Error('Failed to place bid');
  }
};

export const checkStatus = async (itemID: string): Promise<any> => {
  try {
    const response = await api.post(`auction/check-status?itemID=${itemID}`);

    return response.data.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to update status');
    }
    throw new Error('Failed to update status');
  }
};

export const postPayment = async (itemID: string, paymentData: PaymentData): Promise<PaymentResponseDTO> => {
  try {
    const expMonth = paymentData.expMonth.padStart(2, '0');
    const expYear = '20' + paymentData.expYear;
    const formattedPaymentData = {
      ...paymentData,
      expDate: `${expYear}-${expMonth}`,
    };

    const response = await api.post('/auction/pay', formattedPaymentData, {
      params: {
        itemID,
      },
    });

    return response.data.data.receipt;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to process payment');
    }
    throw new Error('Failed to process payment');
  }
};

export const postAuctionItem = async (auctionType: AuctionType, auctionForm: AuctionForm): Promise<AuctionItem> => {
  try {
    if (auctionType == 'dutch') {
      const response = await api.post('/auction/dutch/post', {
        itemName: auctionForm.itemName,
        reservePrice: auctionForm.reservePrice,
        currentPrice: auctionForm.currentPrice,
        shippingTime: auctionForm.shippingTime,
      });

      return response.data.data;
    } else {
      const utcDate = new Date(auctionForm.endDate!.getTime() - auctionForm.endDate!.getTimezoneOffset() * 60000);
      const response = await api.post('/auction/forward/post', {
        itemName: auctionForm.itemName,
        reservePrice: auctionForm.reservePrice,
        currentPrice: auctionForm.currentPrice,
        shippingTime: auctionForm.shippingTime,
        endTime: utcDate.toISOString(),
      });

      return response.data.data;
    }
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to post auction item');
    }
    throw new Error('Failed to post auction item');
  }
};

export const createRating = async (ratedUserId: string, rating: number, feedback: string): Promise<any> => {
  try {
    const ratingRequest: RatingRequestDTO = {
      ratedUserId,
      rating,
      feedback,
    };
    const response = await api.post('/ratings', ratingRequest);
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to create rating');
    }
    throw new Error('Failed to create rating');
  }
};
