'use client';
import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useAuth } from '@/contexts/authContext';
import { AuctionItem } from '@/types';
import { getAuctionDetails } from '@/requests/getRequests';

export default function PaymentPage() {
  const params = useParams();
  const { currentUser } = useAuth();
  const [auction, setAuction] = useState<AuctionItem | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    // TODO: Implement payment processing
    console.log('Processing payment...');
  };

  if (loading) return <div>Loading payment details...</div>;
  if (error) return <div className="text-red-500">{error}</div>;
  if (!auction) return <div>Auction not found</div>;
  console.log(currentUser);
  const totalCost = auction.currentPrice; // Add shipping cost calculation if needed

  return (
    <div className="container mx-auto px-4 py-8">
      <Card className="max-w-3xl mx-auto">
        <CardHeader>
          <CardTitle className="text-2xl text-center">Complete Your Purchase</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
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
                  <Label htmlFor="cardNumber">Card Number</Label>
                  <Input id="cardNumber" placeholder="1234 5678 9012 3456" required />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="cardName">Name on Card</Label>
                  <Input id="cardName" placeholder="John Doe" required />
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="expDate">Expiration Date</Label>
                    <Input id="expDate" placeholder="MM/YY" required />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="securityCode">Security Code</Label>
                    <Input id="securityCode" type="password" maxLength={4} placeholder="***" required />
                  </div>
                </div>
              </div>
            </div>

            <Button type="submit" className="w-full mt-6">
              Submit Payment
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
