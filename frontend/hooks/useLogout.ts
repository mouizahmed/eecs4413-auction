import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { signOut } from '@/requests/postRequests';

export const useLogout = () => {
  const router = useRouter();
  const { setCurrentUser } = useAuth();
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const logout = async () => {
    setIsLoading(true);

    try {
      await signOut();

      // Clear the user from context
      setCurrentUser(null);

      // Redirect to login page or home page
      router.push('/login');
    } catch (error) {
      console.log('Logout error:', error);
    } finally {
      setIsLoading(false);
    }
  };

  return {
    logout,
    isLoading,
  };
};
