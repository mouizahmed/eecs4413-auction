import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/authContext';
import { register } from '@/requests/postRequests';
import { RegisterData } from '@/types';

export const useRegister = () => {
  const router = useRouter();
  const { setCurrentUser } = useAuth();
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const registerUser = async (userData: RegisterData) => {
    setIsLoading(true);
    setError(null);

    try {
      const createdUser = await register(userData);
      // After successful registration, you can:
      // 1. Automatically log the user in
      // 2. Redirect to login page
      // 3. Show success message

      // Option 1: Auto login
      setCurrentUser(createdUser);
      router.push('/');
      return createdUser;
    } catch (error) {
      console.log('Registration error:', error);
      if (error instanceof Error) {
        setError(error.message);
      } else {
        setError('Failed to register');
      }
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  return {
    registerUser,
    error,
    isLoading,
  };
};
