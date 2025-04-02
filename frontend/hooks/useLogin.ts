import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/authContext';
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
      setError((error as Error).message);
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
