'use client';
import React, { useEffect, useState, useMemo } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { CountdownTimer } from '../ui/countdown';
import { getAvailableItems } from '@/requests/getRequests';
import { AuctionItem } from '@/types';
import { Search } from 'lucide-react';
import { useRouter } from 'next/navigation';

export default function AuctionList() {
  const router = useRouter();
  const [auctions, setAuctions] = useState<AuctionItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedAuction, setSelectedAuction] = useState<AuctionItem | null>(null);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    const fetchAuctions = async () => {
      try {
        const response = await getAvailableItems();
        const items = response.map((item: any) => item.auctionItem);
        setAuctions(items);
      } catch (error) {
        setError((error as Error).message);
      } finally {
        setLoading(false);
      }
    };

    fetchAuctions();
  }, []);

  const filteredAuctions = useMemo(() => {
    return auctions.filter((auction) => auction.itemName.toLowerCase().includes(searchQuery.toLowerCase()));
  }, [auctions, searchQuery]);

  const handleAuctionClick = (auction: AuctionItem) => {
    setSelectedAuction(auction);
  };

  const handleBid = async () => {
    if (!selectedAuction) return;
    router.push(`/auction/${selectedAuction.itemID}`);
  };

  const handleStatusUpdate = (updatedAuction: AuctionItem) => {
    setAuctions((prevAuctions) =>
      prevAuctions.map((auction) => (auction.itemID === updatedAuction.itemID ? updatedAuction : auction))
    );
  };

  if (loading) return <div>Loading auctions...</div>;
  if (error) return <div className="text-red-500">{error}</div>;

  return (
    <div className="relative min-h-screen pb-20">
      <div className="mb-6">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500 h-4 w-4" />
          <Input
            type="text"
            placeholder="Search auctions..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {filteredAuctions.map((auction) => (
          <Card
            key={auction.itemID}
            className={`cursor-pointer transition-all duration-200 ${
              selectedAuction?.itemID === auction.itemID ? 'border-2 border-primary shadow-lg' : 'hover:shadow-md'
            }`}
            onClick={() => handleAuctionClick(auction)}
          >
            <CardHeader>
              <CardTitle>{auction.itemName}</CardTitle>
            </CardHeader>
            <CardContent>
              <p>Current Price: ${auction.currentPrice}</p>
              <p>Auction Type: {auction.auctionType}</p>
              <p>Status: {auction.auctionStatus}</p>
              {auction.auctionType === 'FORWARD' && (
                <CountdownTimer endTime={auction.endTime} itemId={auction.itemID} onStatusUpdate={handleStatusUpdate} />
              )}
              {auction.auctionType === 'DUTCH' && <p className="text-sm text-gray-600">Time remaining: NOW</p>}
            </CardContent>
          </Card>
        ))}
      </div>

      <div className="fixed bottom-6 right-6 z-50">
        <Button size="lg" className="shadow-lg" onClick={handleBid} disabled={!selectedAuction}>
          {selectedAuction ? `Bid on ${selectedAuction.itemName}` : 'Select an item to bid'}
        </Button>
      </div>
    </div>
  );
}
