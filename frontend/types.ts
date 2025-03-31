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
