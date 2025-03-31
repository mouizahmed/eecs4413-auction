import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { signOut } from '@/requests/postRequests';
import { useAuth } from '@/contexts/authContext';

export const useLogout = () => {
  const router = useRouter();
  const { setCurrentUser } = useAuth();
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const logout = async () => {
    setIsLoading(true);

    try {
      await signOut();
      setCurrentUser(null);
      router.push('/login');
    } catch (error) {
      console.log(error);
    } finally {
      setIsLoading(false);
    }
  };

  return {
    logout,
    isLoading,
  };
};
