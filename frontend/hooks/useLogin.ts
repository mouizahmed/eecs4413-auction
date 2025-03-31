import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { doSignIn } from '@/contexts/auth';

export const useLogin = () => {
  const router = useRouter();
  const { setCurrentUser } = useAuth();
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const login = async (username: string, password: string) => {
    setIsLoading(true);
    setError(null);

    try {
      const userData = await doSignIn(username, password);
      setCurrentUser(userData);
      router.push('/');
      return userData;
    } catch (error) {
      console.log('Login error:', error);
      if (error instanceof Error) {
        setError(error.message);
      } else {
        setError('Failed to login');
      }
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  return {
    login,
    error,
    isLoading,
  };
};
