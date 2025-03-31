'use client';
import React, { useEffect } from 'react';
import { LoginForm } from '@/components/auth/LoginForm';
import { useAuth } from '@/contexts/AuthContext';
import { useRouter } from 'next/navigation';

export default function Page() {
  const { userLoggedIn } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (userLoggedIn) {
      router.push('/');
    }
  }, [userLoggedIn, router]);

  return (
    <div className="flex min-h-[calc(100vh-160px)] w-full flex-col items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-sm">
        <LoginForm />
      </div>
    </div>
  );
}
