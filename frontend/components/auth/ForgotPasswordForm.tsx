'use client';
import React, { useState } from 'react';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { getSecurityQuestion } from '@/requests/getRequests';
import { forgotPassword } from '@/requests/postRequests';

export function ForgotPasswordForm({ onBackToLogin }: { onBackToLogin: () => void }) {
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [formStep, setFormStep] = useState<'username' | 'security-question' | 'success'>('username');
  const [securityQuestion, setSecurityQuestion] = useState<string>('');
  const [formData, setFormData] = useState({
    username: '',
    securityAnswer: '',
    newPassword: '',
  });

  const handleRetrieveSecurityQuestion = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    try {
      setSecurityQuestion(await getSecurityQuestion(formData.username));
      setFormStep('security-question');
    } catch (error) {
      setError((error as Error).message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleResetPassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    try {
      await forgotPassword(formData);
      setFormStep('success');
    } catch (error) {
      setError((error as Error).message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { id, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [id]: value,
    }));
  };

  const renderUsernameStep = () => (
    <>
      <CardHeader>
        <CardTitle className="text-2xl">Forgot Password</CardTitle>
        <CardDescription>Enter your username</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleRetrieveSecurityQuestion}>
          <div className="flex flex-col gap-6">
            {error && <div className="text-red-500 text-sm">{error}</div>}
            <div className="grid gap-2">
              <div className="flex items-center">
                <Label htmlFor="username">Username</Label>
              </div>
              <Input id="username" type="text" required value={formData.username} onChange={handleChange} />
            </div>
            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? 'Please wait...' : 'Retrieve Security Question'}
            </Button>

            <Button type="button" variant="outline" className="w-full" onClick={onBackToLogin}>
              Back to Login
            </Button>
          </div>
        </form>
      </CardContent>
    </>
  );

  const renderSecurityQuestionStep = () => (
    <>
      <CardHeader>
        <CardTitle className="text-2xl">Security Question</CardTitle>
        <CardDescription>Please answer your security question</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleResetPassword}>
          <div className="flex flex-col gap-6">
            {error && <div className="text-red-500 text-sm">{error}</div>}
            <div className="grid gap-2">
              <div className="flex items-center">
                <Label htmlFor="securityQuestion">Question</Label>
              </div>
              <div className="p-2 bg-muted rounded-md">{securityQuestion}</div>
            </div>
            <div className="grid gap-2">
              <div className="flex items-center">
                <Label htmlFor="securityAnswer">Your Answer</Label>
              </div>
              <Input id="securityAnswer" type="text" required value={formData.securityAnswer} onChange={handleChange} />
            </div>
            <div className="grid gap-2">
              <div className="flex items-center">
                <Label htmlFor="newPassword">New Password</Label>
              </div>
              <Input id="newPassword" type="password" required value={formData.newPassword} onChange={handleChange} />
            </div>
            <div className="grid gap-2"></div>
            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? 'Verifying...' : 'Verify Answer'}
            </Button>

            <Button type="button" variant="outline" className="w-full" onClick={() => setFormStep('username')}>
              Back
            </Button>
          </div>
        </form>
      </CardContent>
    </>
  );

  const renderSuccessStep = () => (
    <>
      <CardHeader>
        <CardTitle className="text-2xl">Success!</CardTitle>
        <CardDescription>You can now go back to log in with your new password.</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="flex flex-col gap-6">
          <div className="grid gap-2"></div>
          <Button type="button" variant="outline" className="w-full" onClick={onBackToLogin}>
            Back to Login
          </Button>
        </div>
      </CardContent>
    </>
  );

  return (
    <div className={cn('flex flex-col gap-6')}>
      <Card>
        {formStep === 'username' && renderUsernameStep()}
        {formStep === 'security-question' && renderSecurityQuestionStep()}
        {formStep === 'success' && renderSuccessStep()}
      </Card>
    </div>
  );
}
