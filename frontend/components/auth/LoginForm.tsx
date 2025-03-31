'use client';
import React, { useState } from 'react';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import Link from 'next/link';
import { useLogin } from '@/hooks/useLogin';
import { ForgotPasswordForm } from './ForgotPasswordForm';

export function LoginForm() {
  const { login, error, isLoading } = useLogin();
  const [showForgotPassword, setShowForgotPassword] = useState<boolean>(false);
  const [formData, setFormData] = useState({
    username: '',
    password: '',
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await login(formData.username, formData.password);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { id, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [id]: value,
    }));
  };

  if (showForgotPassword) {
    return (
      <div className={cn('flex flex-col gap-6')}>
        <ForgotPasswordForm onBackToLogin={() => setShowForgotPassword(false)} />
      </div>
    );
  }

  return (
    <div className={cn('flex flex-col gap-6')}>
      <Card>
        <>
          <CardHeader>
            <CardTitle className="text-2xl">Login</CardTitle>
            <CardDescription>Enter your username and password below to login to your account</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit}>
              <div className="flex flex-col gap-6">
                {error && <div className="text-red-500 text-sm">{error}</div>}
                <div className="grid gap-2">
                  <div className="flex items-center">
                    <Label htmlFor="username">Username</Label>
                  </div>
                  <Input id="username" type="text" required value={formData.username} onChange={handleChange} />
                </div>

                <div className="grid gap-2">
                  <div className="flex items-center">
                    <Label htmlFor="password">Password</Label>
                    <Link
                      href="#"
                      onClick={(e) => {
                        e.preventDefault();
                        setShowForgotPassword(true);
                      }}
                      className="ml-auto inline-block text-sm underline-offset-4 hover:underline"
                    >
                      Forgot your password?
                    </Link>
                  </div>
                  <Input id="password" type="password" required value={formData.password} onChange={handleChange} />
                </div>

                <Button type="submit" className="w-full" disabled={isLoading}>
                  {isLoading ? 'Please wait...' : 'Login'}
                </Button>
              </div>
              <div className="mt-4 text-center text-sm">
                Don&apos;t have an account?{' '}
                <Link href="/register" className="underline underline-offset-4">
                  Sign up
                </Link>
              </div>
            </form>
          </CardContent>
        </>
      </Card>
    </div>
  );
}
