'use client';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/contexts/authContext';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { DateTimePicker } from '@/components/ui/date-time-picker';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { AuctionForm, AuctionItem, AuctionType } from '@/types';
import { postAuctionItem } from '@/requests/postRequests';

export default function PostAuction() {
  const router = useRouter();
  const { userLoggedIn } = useAuth();
  const [auctionType, setAuctionType] = useState<AuctionType>('forward');
  const [formData, setFormData] = useState<AuctionForm>({
    itemName: '',
    shippingTime: 0,
    currentPrice: 0,
    endDate: new Date(),
    reservePrice: 0,
  });
  const [error, setError] = useState<string>('');

  if (!userLoggedIn) {
    router.push('/login');
    return null;
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      const newAuctionItem: AuctionItem = await postAuctionItem(auctionType, formData);
      router.push(`/auction/${newAuctionItem.itemID}`);
    } catch (error) {
      setError((error as Error).message);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleDateChange = (date: Date) => {
    setFormData((prev) => ({
      ...prev,
      endDate: date,
    }));
  };

  return (
    <div className="flex min-h-[calc(100vh-160px)] w-full flex-col items-center justify-center p-6 md:p-10">
      <Card className="w-full max-w-2xl">
        <CardHeader>
          <CardTitle className="text-2xl">Post New Auction</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            {error && <div className="rounded-md bg-red-50 p-4 text-sm text-red-700">{error}</div>}
            <div className="space-y-2">
              <Label>Auction Type</Label>
              <RadioGroup
                value={auctionType}
                onValueChange={(value: AuctionType) => setAuctionType(value)}
                className="flex gap-4"
              >
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="forward" id="forward" />
                  <Label htmlFor="forward">Forward Auction</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="dutch" id="dutch" />
                  <Label htmlFor="dutch">Dutch Auction</Label>
                </div>
              </RadioGroup>
            </div>

            <div className="space-y-2">
              <Label htmlFor="itemName">Item Name</Label>
              <Input id="itemName" name="itemName" value={formData.itemName} onChange={handleChange} required />
            </div>

            <div className="space-y-2">
              <Label htmlFor="shippingTime">Shipping Time (days)</Label>
              <Input
                type="number"
                id="shippingTime"
                name="shippingTime"
                value={formData.shippingTime}
                onChange={handleChange}
                required
                min="1"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="currentPrice">Starting Price ($)</Label>
              <Input
                type="number"
                id="currentPrice"
                name="currentPrice"
                value={formData.currentPrice}
                onChange={handleChange}
                required
                min="0"
                step="0.01"
              />
            </div>

            {auctionType === 'forward' ? (
              <div className="space-y-2">
                <Label>End Date</Label>
                <DateTimePicker value={formData.endDate} onChange={handleDateChange} />
              </div>
            ) : (
              <div className="space-y-2">
                <Label htmlFor="reservePrice">Reserve Price ($)</Label>
                <Input
                  type="number"
                  id="reservePrice"
                  name="reservePrice"
                  value={formData.reservePrice}
                  onChange={handleChange}
                  required
                  min="0"
                  step="0.01"
                />
              </div>
            )}

            <Button type="submit" className="w-full">
              Create Auction
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
