'use client';
import { AuthenticationContext, ReactChildren, User } from '@/types';
import React, { createContext, useContext, useState, useEffect } from 'react';
import { checkAuthentication } from './auth';

const initialState: AuthenticationContext = {
  userLoggedIn: false,
  currentUser: null,
  setCurrentUser: () => {},
};

const AuthContext = createContext<AuthenticationContext>(initialState);

export function useAuth() {
  return useContext(AuthContext);
}

export function AuthProvider({ children }: ReactChildren) {
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [authChecked, setAuthChecked] = useState<boolean>(false);

  // Initialize auth state on mount
  useEffect(() => {
    const initAuth = async () => {
      // Skip if we've already checked auth
      if (authChecked) return;

      try {
        // With cookie-based auth, we just need to check with the server
        const user = await checkAuthentication();
        if (user) {
          setCurrentUser(user);
        } else {
          setCurrentUser(null);
        }
      } catch (error) {
        console.log('Auth initialization error:', error);
        setCurrentUser(null);
      } finally {
        setLoading(false);
        setAuthChecked(true);
      }
    };

    initAuth();
  }, [authChecked]);

  const value: AuthenticationContext = {
    userLoggedIn: !!currentUser,
    currentUser,
    setCurrentUser,
  };

  return <AuthContext.Provider value={value}>{!loading && children}</AuthContext.Provider>;
}
