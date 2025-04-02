'use client';
import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import Link from 'next/link';
import { register } from '@/requests/postRequests';
import { Separator } from '@/components/ui/separator';
import { RegisterData } from '@/types';

export function RegisterForm() {
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [success, setSuccess] = useState<boolean>(false);
  const [formData, setFormData] = useState<RegisterData>({
    username: '',
    password: '',
    firstName: '',
    lastName: '',
    streetName: '',
    streetNum: 0,
    postalCode: '',
    city: '',
    province: '',
    country: '',
    securityQuestion: '',
    securityAnswer: '',
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { id, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [id]: id === 'streetNum' ? (value === '' ? 0 : parseInt(value)) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await register(formData);
      setSuccess(true);
    } catch (error) {
      setError((error as Error).message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={'flex flex-col gap-6'}>
      <Card>
        {!success ? (
          <>
            <CardHeader>
              <CardTitle className="text-2xl">Register</CardTitle>
              <CardDescription>Create your account</CardDescription>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleSubmit}>
                <div className="flex flex-col gap-6">
                  {error && <div className="text-red-500 text-sm">{error}</div>}
                  <div className="grid gap-2">
                    <Label htmlFor="username">Username</Label>
                    <Input id="username" required value={formData.username} onChange={handleChange} />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="firstName">First Name</Label>
                    <Input id="firstName" required value={formData.firstName} onChange={handleChange} />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="lastName">Last Name</Label>
                    <Input id="lastName" required value={formData.lastName} onChange={handleChange} />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="password">Password</Label>
                    <Input id="password" type="password" required value={formData.password} onChange={handleChange} />
                  </div>
                  <Separator />
                  <div className="grid gap-2">
                    <Label htmlFor="streetName">Street Name</Label>
                    <Input id="streetName" required value={formData.streetName} onChange={handleChange} />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="streetNum">Street Number</Label>
                    <Input id="streetNum" type="number" required value={formData.streetNum} onChange={handleChange} />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="postalCode">Postal Code</Label>
                    <Input id="postalCode" required value={formData.postalCode} onChange={handleChange} />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="city">City</Label>
                    <Input id="city" required value={formData.city} onChange={handleChange} />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="province">Province</Label>
                    <Input id="province" required value={formData.province} onChange={handleChange} />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="country">Country</Label>
                    <Input id="country" required value={formData.country} onChange={handleChange} />
                  </div>
                  <Separator />
                  <div className="grid gap-2">
                    <Label htmlFor="securityQuestion">Security Question</Label>
                    <Input id="securityQuestion" required value={formData.securityQuestion} onChange={handleChange} />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="securityAnswer">Security Answer</Label>
                    <Input id="securityAnswer" required value={formData.securityAnswer} onChange={handleChange} />
                  </div>
                  <Button type="submit" className="w-full" disabled={loading}>
                    {loading ? 'Registering...' : 'Register'}
                  </Button>
                </div>
                <div className="mt-4 text-center text-sm">
                  Already have an account?{' '}
                  <Link href="/login" className="underline underline-offset-4">
                    Log In
                  </Link>
                </div>
              </form>
            </CardContent>
          </>
        ) : (
          <>
            <CardHeader>
              <CardTitle className="text-2xl">Registration Successful!</CardTitle>
              <CardDescription>Your account has been created successfully.</CardDescription>
            </CardHeader>
          </>
        )}
      </Card>
    </div>
  );
}
