'use client';
import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { CountdownTimer } from '@/components/ui/countdown';
import { AuctionItem } from '@/types';
import axios from 'axios';
import { placeBid } from '@/requests/postRequests';
import { useAuth } from '@/contexts/authContext';
import { useAuctionWebSocket } from '@/hooks/useAuctionWebsocket';
import { BidHistory } from '@/components/display/BidHistory';

export default function ForwardAuctionPage() {
  const params = useParams();
  const { currentUser } = useAuth();
  const [auction, setAuction] = useState<AuctionItem | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [bidAmount, setBidAmount] = useState<string>('');
  const [isPaying, setIsPaying] = useState(false);
  const { lastMessage, socketStatus, isSubscribed } = useAuctionWebSocket(String(params.id));

  // Handle WebSocket messages
  useEffect(() => {
    if (!lastMessage || !auction) return;

    // Handle different message types
    if (lastMessage.type === 'AUCTION_UPDATE') {
      setAuction((prevAuction) => {
        if (!prevAuction) return null;

        return {
          ...prevAuction,
          currentPrice: lastMessage.currentPrice,
          highestBidderUsername: lastMessage.highestBidder,
          auctionStatus: lastMessage.auctionStatus,
        };
      });
    }

    if (lastMessage.type === 'BID_PLACED') {
      setAuction((prevAuction) => {
        if (!prevAuction) return null;

        const newBid = {
          bidID: lastMessage.bidId,
          itemID: lastMessage.itemId,
          userID: lastMessage.userId,
          username: lastMessage.username,
          bidAmount: lastMessage.bidAmount,
          timestamp: lastMessage.timestamp,
        };
        console.log('NEW BID');
        // Check if we already have this bid
        const existingBidIndex = prevAuction.bids?.findIndex((bid) => bid.bidID === newBid.bidID);

        // If this bid already exists, don't add it
        if (existingBidIndex !== undefined && existingBidIndex >= 0) {
          return prevAuction;
        }

        return {
          ...prevAuction,
          currentPrice: lastMessage.currentPrice || prevAuction.currentPrice,
          highestBidderUsername:
            lastMessage.bidAmount >= prevAuction.currentPrice
              ? lastMessage.username
              : prevAuction.highestBidderUsername,
          bids: [newBid, ...(prevAuction.bids || [])],
        };
      });
    }
  }, [lastMessage, auction]);

  useEffect(() => {
    const fetchAuction = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/auction/get-by-id?itemID=${params.id}`, {
          withCredentials: true,
        });

        setAuction({
          ...response.data.data,
        });
      } catch (error) {
        setError('Failed to fetch auction details');
      } finally {
        setLoading(false);
      }
    };

    fetchAuction();
  }, [params.id]);

  const handleBid = async () => {
    if (!auction || !bidAmount) return;
    try {
      await placeBid(auction.itemID, Number(bidAmount));
      setBidAmount('');
      // No need to refresh auction data after placing bid
      // The WebSocket will provide the update
    } catch (err) {
      console.error('Error placing bid:', err);
    }
  };

  const handlePay = async () => {
    if (!auction) return;
    setIsPaying(true);
    try {
      // TODO: Implement payment API call
      await new Promise((resolve) => setTimeout(resolve, 1000)); // Simulated API call
      setAuction((prev) => (prev ? { ...prev, auctionStatus: 'PAID' } : null));
    } catch (err) {
      console.error('Error processing payment:', err);
    } finally {
      setIsPaying(false);
    }
  };

  const isHighestBidder = currentUser && auction?.highestBidderUsername === currentUser.username;
  const isAuctionEnded = auction?.auctionStatus === 'SOLD';
  const canPay = isHighestBidder && isAuctionEnded && auction?.auctionStatus !== 'PAID';

  if (loading) return <div>Loading auction details...</div>;
  if (error) return <div className="text-red-500">{error}</div>;
  if (!auction) return <div>Auction not found</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <Card className="max-w-2xl mx-auto">
        <CardHeader>
          <CardTitle className="text-2xl">{auction.itemName}</CardTitle>
          <div className="mt-2 flex items-center gap-2">
            <div
              className={`w-2 h-2 rounded-full ${
                socketStatus === 'OPEN'
                  ? 'bg-green-500'
                  : socketStatus === 'CONNECTING'
                  ? 'bg-yellow-500'
                  : 'bg-red-500'
              }`}
            />
            <span className="text-sm text-gray-500">
              {socketStatus === 'OPEN' && isSubscribed
                ? 'Connected & Subscribed'
                : socketStatus === 'OPEN'
                ? 'Connected'
                : socketStatus === 'CONNECTING'
                ? 'Connecting...'
                : 'Disconnected'}
            </span>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <p className="text-sm text-gray-500">Current Price</p>
              <p className="text-xl font-semibold">${auction.currentPrice}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Status</p>
              <p className="text-xl font-semibold">{auction.auctionStatus}</p>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <p className="text-sm text-gray-500">Shipping Time</p>
              <p className="text-xl font-semibold">{auction.shippingTime}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Remaining</p>
              <CountdownTimer
                endTime={auction.endTime}
                itemId={auction.itemID}
                onStatusUpdate={(updatedAuction) =>
                  setAuction((prev) => ({ ...(prev as AuctionItem), ...updatedAuction }))
                }
              />
            </div>
          </div>

          {auction.highestBidderUsername && (
            <div className="bg-primary/5 p-3 rounded-lg">
              <p className="text-sm text-gray-500">Current Highest Bidder</p>
              <p className="text-lg font-semibold text-primary">{auction.highestBidderUsername}</p>
            </div>
          )}

          {auction.auctionStatus === 'AVAILABLE' && (
            <div className="pt-4 border-t">
              <div className="space-y-4">
                <div>
                  <label htmlFor="bidAmount" className="block text-sm font-medium text-gray-700">
                    Your Bid Amount
                  </label>
                  <Input
                    id="bidAmount"
                    type="number"
                    min={String(auction.currentPrice + 1)}
                    value={bidAmount}
                    onChange={(e) => setBidAmount(e.target.value)}
                    className="mt-1"
                  />
                </div>
                <Button
                  className="w-full"
                  onClick={handleBid}
                  disabled={!bidAmount || Number(bidAmount) <= auction.currentPrice}
                >
                  Place Bid
                </Button>
              </div>
            </div>
          )}

          {canPay && (
            <div className="pt-4 border-t">
              <div className="space-y-4">
                <p className="text-sm text-gray-500">Congratulations! You won the auction!</p>
                <Button className="w-full" onClick={handlePay} disabled={isPaying}>
                  {isPaying ? 'Processing Payment...' : 'Pay Now'}
                </Button>
              </div>
            </div>
          )}

          {auction.auctionStatus === 'PAID' && (
            <div className="pt-4 border-t">
              <div className="bg-green-50 p-4 rounded-lg">
                <p className="text-green-700 font-medium">Payment completed successfully!</p>
              </div>
            </div>
          )}

          {auction.bids && <BidHistory bids={auction.bids} currentPrice={auction.currentPrice} />}
        </CardContent>
      </Card>
    </div>
  );
}
