import { ReactNode } from 'react';

export interface AuthenticationContext {
  userLoggedIn: boolean;
  currentUser: User | null;
  setCurrentUser: React.Dispatch<React.SetStateAction<User | null>>;
}

export interface User {
  firstName: string;
  lastName: string;
  streetAddress: string;
  streetNumber: number;
  postalCode: string;
  city: string;
  country: string;
  username: string;
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
  country: string;
  securityQuestion: string;
  securityAnswer: string;
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
  bids: Bid[];
}

export type AuctionType = 'forward' | 'dutch';
