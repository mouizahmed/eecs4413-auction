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
