'use client';
import React, { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useAuth } from '@/contexts/authContext';
import { AuctionItem, PaymentData } from '@/types';
import { getAuctionDetails } from '@/requests/getRequests';
import { postPayment } from '@/requests/postRequests';

export default function PaymentPage() {
  const params = useParams();
  const router = useRouter();
  const { currentUser } = useAuth();
  const [auction, setAuction] = useState<AuctionItem | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState<PaymentData>({
    cardNum: '',
    cardName: '',
    expMonth: '',
    expYear: '',
    securityCode: '',
  });

  const [submitError, setSubmitError] = useState<string | null>(null);

  const validateCardNumber = (value: string) => {
    const cleaned = value.replace(/\s/g, '');
    if (!/^\d{16}$/.test(cleaned)) {
      return 'Card number must be 16 digits';
    }
    return '';
  };

  const validateCardName = (value: string) => {
    if (!/^[A-Za-z\s]+$/.test(value)) {
      return 'Name can only contain letters and spaces';
    }
    return '';
  };

  const validateExpMonth = (value: string) => {
    const month = parseInt(value);
    if (isNaN(month) || month < 1 || month > 12) {
      return 'Month must be between 01 and 12';
    }
    return '';
  };

  const validateExpYear = (value: string) => {
    const year = parseInt(value);
    const currentYear = new Date().getFullYear() % 100;
    if (isNaN(year) || year < currentYear) {
      return 'Year must be current year or future';
    }
    return '';
  };

  const validateExpiration = (month: string, year: string) => {
    const currentDate = new Date();
    const currentMonth = currentDate.getMonth() + 1; // getMonth() returns 0-11
    const currentYear = currentDate.getFullYear() % 100;
    const expMonth = parseInt(month);
    const expYear = parseInt(year);

    if (expYear === currentYear && expMonth < currentMonth) {
      return 'Card has expired';
    }
    return '';
  };

  const validateSecurityCode = (value: string) => {
    if (!/^\d{3,4}$/.test(value)) {
      return 'Security code must be 3-4 digits';
    }
    return '';
  };

  useEffect(() => {
    const fetchAuction = async () => {
      try {
        const auctionItem = await getAuctionDetails(String(params.id));
        setAuction(auctionItem);
      } catch (error) {
        setError('Failed to fetch auction details');
      } finally {
        setLoading(false);
      }
    };

    fetchAuction();
  }, [params.id]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { id, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [id]: value,
    }));
    // Clear submit error when user starts typing
    setSubmitError(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitError(null);
    setIsSubmitting(true);

    // Validate all fields
    const errors = {
      cardName: validateCardName(formData.cardName),
      expMonth: validateExpMonth(formData.expMonth),
      expYear: validateExpYear(formData.expYear),
      expiration: validateExpiration(formData.expMonth, formData.expYear),
      securityCode: validateSecurityCode(formData.securityCode),
    };

    // Find the first error message
    const firstError = Object.values(errors).find((error) => error !== '');
    if (firstError) {
      setSubmitError(firstError);
      setIsSubmitting(false);
      return;
    }

    try {
      const response = await postPayment(String(params.id), formData);
      console.log(response);
      router.push(`/receipt/${response.receiptID}`);
    } catch (error) {
      setSubmitError(error instanceof Error ? error.message : 'Failed to process payment');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) return <div>Loading payment details...</div>;
  if (error) return <div className="text-red-500">{error}</div>;
  if (!auction) return <div>Auction not found</div>;

  // Check if auction is sold
  if (auction.auctionStatus !== 'SOLD') {
    return (
      <div className="container mx-auto px-4 py-8">
        <Card className="max-w-3xl mx-auto">
          <CardHeader>
            <CardTitle className="text-2xl text-center text-red-600">Payment Not Available</CardTitle>
          </CardHeader>
          <CardContent className="text-center">
            <p className="text-lg mb-4">Payment is not available for this item.</p>
            <p className="text-gray-600">The auction status is: {auction.auctionStatus}</p>
            <p className="text-gray-600 mt-2">Payment can only be processed for sold items.</p>
          </CardContent>
        </Card>
      </div>
    );
  }

  // Check if current user is the highest bidder
  if (currentUser?.username !== auction.highestBidderUsername) {
    return (
      <div className="container mx-auto px-4 py-8">
        <Card className="max-w-3xl mx-auto">
          <CardHeader>
            <CardTitle className="text-2xl text-center text-red-600">Access Denied</CardTitle>
          </CardHeader>
          <CardContent className="text-center">
            <p className="text-lg mb-4">You are not authorized to make payment for this auction.</p>
            <p className="text-gray-600">Only the highest bidder can complete the payment.</p>
            <p className="text-gray-600 mt-2">Highest bidder: {auction.highestBidderUsername}</p>
          </CardContent>
        </Card>
      </div>
    );
  }

  const totalCost = auction.currentPrice;

  return (
    <div className="container mx-auto px-4 py-8">
      <Card className="max-w-3xl mx-auto">
        <CardHeader>
          <CardTitle className="text-2xl text-center">Complete Your Purchase</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            {submitError && (
              <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
                <p className="text-red-600">{submitError}</p>
              </div>
            )}
            <div className="grid grid-cols-2 gap-8">
              {/* Shipping Information */}
              <div className="space-y-4">
                <h3 className="text-lg font-semibold">Winning Bidder Information</h3>
                <div className="space-y-2">
                  <Label htmlFor="firstName">First Name</Label>
                  <Input id="firstName" value={currentUser?.firstName || ''} disabled className="bg-muted" />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="lastName">Last Name</Label>
                  <Input id="lastName" value={currentUser?.lastName || ''} disabled className="bg-muted" />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="street">Street Address</Label>
                  <div className="flex gap-2">
                    <Input id="streetNum" value={currentUser?.streetNum} disabled className="bg-muted w-24" />
                    <Input id="streetName" value={currentUser?.streetName || ''} disabled className="bg-muted flex-1" />
                  </div>
                </div>
                <div className="space-y-2">
                  <Label htmlFor="province">Province</Label>
                  <Input id="province" value={currentUser?.province || ''} disabled className="bg-muted" />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="country">Country</Label>
                  <Input id="country" value={currentUser?.country || ''} disabled className="bg-muted" />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="postalCode">Postal Code</Label>
                  <Input id="postalCode" value={currentUser?.postalCode || ''} disabled className="bg-muted" />
                </div>
                <div className="mt-4 p-4 bg-primary/5 rounded-lg">
                  <div className="flex justify-between items-center">
                    <span className="font-semibold">Total Cost:</span>
                    <span className="text-xl font-bold">${totalCost.toFixed(2)}</span>
                  </div>
                </div>
              </div>

              {/* Payment Information */}
              <div className="space-y-4">
                <h3 className="text-lg font-semibold">Payment Details</h3>
                <div className="space-y-2">
                  <Label htmlFor="cardNum">Card Number</Label>
                  <Input
                    id="cardNum"
                    placeholder="1234 5678 9012 3456"
                    required
                    value={formData.cardNum}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="cardName">Name on Card</Label>
                  <Input
                    id="cardName"
                    placeholder="John Doe"
                    required
                    value={formData.cardName}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="expMonth">Month</Label>
                    <Input
                      id="expMonth"
                      type="number"
                      min="1"
                      max="12"
                      placeholder="MM"
                      maxLength={2}
                      required
                      value={formData.expMonth}
                      onChange={handleInputChange}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="expYear">Year</Label>
                    <Input
                      id="expYear"
                      type="number"
                      min={new Date().getFullYear() % 100}
                      max="99"
                      placeholder="YY"
                      maxLength={2}
                      required
                      value={formData.expYear}
                      onChange={handleInputChange}
                    />
                  </div>
                </div>
                <div className="space-y-2">
                  <Label htmlFor="securityCode">Security Code</Label>
                  <Input
                    id="securityCode"
                    type="password"
                    maxLength={4}
                    placeholder="***"
                    required
                    value={formData.securityCode}
                    onChange={handleInputChange}
                  />
                </div>
              </div>
            </div>

            <Button type="submit" className="w-full mt-6" disabled={isSubmitting}>
              {isSubmitting ? 'Processing Payment...' : 'Submit Payment'}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
