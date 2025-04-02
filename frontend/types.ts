import { ReactNode } from 'react';

export interface AuthenticationContext {
  userLoggedIn: boolean;
  currentUser: User | null;
  setCurrentUser: React.Dispatch<React.SetStateAction<User | null>>;
}

export interface User {
  firstName: string;
  lastName: string;
  streetName: string;
  streetNum: number;
  postalCode: string;
  city: string;
  province: string;
  country: string;
  username: string;
  userID: string;
  password: string;
}

export type ReactChildren = {
  children: ReactNode;
};

export interface RegisterData {
  username: string;
  password: string;
  firstName: string;
  lastName: string;
  streetName: string;
  streetNum: number;
  postalCode: string;
  city: string;
  province: string;
  country: string;
  securityQuestion: string;
  securityAnswer: string;
}

export interface PaymentData {
  cardNum: string;
  cardName: string;
  expMonth: string;
  expYear: string;
  securityCode: string;
}

export interface PaymentResponseDTO {
  receiptID: string;
  itemID: string;
  userID: string;
  username: string;
  totalCost: number;
  shippingTime: number;
  timestamp: string;
}

export interface ForgotPasswordData {
  username: string;
  newPassword: string;
  securityAnswer: string;
}

export interface Bid {
  bidID: string;
  itemID: string;
  userID: string;
  username: string;
  bidAmount: number;
  timestamp: string;
}

export interface AuctionItem {
  itemID: string;
  itemName: string;
  currentPrice: number;
  auctionStatus: string;
  auctionType: string;
  endTime: string;
  shippingTime: string;
  highestBidderUsername?: string;
  sellerUsername: string;
  sellerID: string;
  bids: Bid[];
  reservePrice?: number;
}

export interface AuctionForm {
  itemName: string;
  shippingTime: number;
  currentPrice: number;
  endDate?: Date;
  reservePrice?: number;
}

export type AuctionType = 'forward' | 'dutch';

export interface ReceiptResponseDTO {
  receiptID: string;
  itemID: string;
  userID: string;
  username: string;
  sellerID: string;
  sellerUsername: string;
  totalCost: number;
  creditCard: {
    cardNum: string;
    cardName: string;
    expDate: {
      month: number;
      year: number;
    };
    securityCode: string;
  };
  address: {
    streetNum: number;
    streetName: string;
    city: string;
    province: string;
    postalCode: string;
    country: string;
  };
  shippingTime: number;
  timestamp: string;
}

export interface RatingRequestDTO {
  ratedUserId: string;
  rating: number;
  feedback: string;
}
