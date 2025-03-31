'use client';
import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { CountdownTimer } from '@/components/ui/countdown';
import { AuctionItem, ConnectionStatus, WebSocketMessage } from '@/types';
import axios from 'axios';
import { placeBid } from '@/requests/postRequests';
import { webSocketService } from '@/services/websocket';

export default function ForwardAuctionPage() {
  const params = useParams();
  const [auction, setAuction] = useState<AuctionItem | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [bidAmount, setBidAmount] = useState<string>('');
  const [wsStatus, setWsStatus] = useState<ConnectionStatus>('connecting');

  useEffect(() => {
    const fetchAuction = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/auction/get-by-id?itemID=${params.id}`, {
          withCredentials: true,
        });
        setAuction(response.data.data);
      } catch (error) {
        setError('Failed to fetch auction details');
      } finally {
        setLoading(false);
      }
    };

    const handleWebSocketMessage = (message: WebSocketMessage) => {
      setAuction((prevAuction) => {
        if (!prevAuction) return prevAuction;

        if (message.type === 'BID_PLACED' && message.bidId && message.userId && message.bidAmount) {
          const newBid = {
            bidID: message.bidId,
            itemID: message.itemId,
            userID: message.userId,
            username: message.username || 'Unknown',
            bidAmount: message.bidAmount,
            timestamp: new Date().toISOString(),
          };

          return {
            ...prevAuction,
            currentPrice: message.currentPrice,
            bids: [newBid, ...(prevAuction.bids || [])],
            highestBidderUsername: message.username,
          };
        }

        return {
          ...prevAuction,
          currentPrice: message.currentPrice,
          ...(message.auctionStatus && { auctionStatus: message.auctionStatus }),
        };
      });
    };

    fetchAuction();
    const unsubStatus = webSocketService.subscribeToStatus(setWsStatus);
    const unsubAuction = webSocketService.subscribe(params.id as string, handleWebSocketMessage);

    return () => {
      unsubStatus();
      unsubAuction();
    };
  }, [params.id]);

  const handleBid = async () => {
    if (!auction || !bidAmount) return;
    try {
      await placeBid(auction.itemID, Number(bidAmount));
      setBidAmount('');
    } catch (err) {
      console.error('Error placing bid:', err);
    }
  };

  if (loading) return <div>Loading auction details...</div>;
  if (error) return <div className="text-red-500">{error}</div>;
  if (!auction) return <div>Auction not found</div>;

  const ConnectionStatus = () => (
    <div className="flex items-center gap-2 text-sm">
      <div
        className={`w-2 h-2 rounded-full ${
          wsStatus === 'connected'
            ? 'bg-green-500'
            : wsStatus === 'connecting'
            ? 'bg-yellow-500'
            : wsStatus === 'error'
            ? 'bg-red-500'
            : 'bg-gray-500'
        }`}
      />
      <span className="text-gray-600">
        {wsStatus === 'connected'
          ? 'Live Updates Active'
          : wsStatus === 'connecting'
          ? 'Connecting...'
          : wsStatus === 'error'
          ? 'Connection Error'
          : 'Disconnected - Retrying...'}
      </span>
    </div>
  );

  const BidHistory = () =>
    auction.bids &&
    auction.bids.length > 0 && (
      <div className="pt-4 border-t">
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-lg font-semibold">Bid History</h3>
          <span className="text-sm text-gray-500">{auction.bids.length} bids</span>
        </div>
        <div className="space-y-2">
          {auction.bids.map((bid) => (
            <div
              key={bid.bidID}
              className="flex justify-between items-center p-3 bg-white border rounded-lg shadow-sm hover:shadow-md transition-shadow"
            >
              <div>
                <p className="font-medium text-gray-900">{bid.username}</p>
                <p className="text-sm text-gray-500">{new Date(bid.timestamp).toLocaleString()}</p>
              </div>
              <div className="text-right">
                <p className="font-semibold text-lg text-primary">${bid.bidAmount}</p>
                <p className="text-xs text-gray-500">
                  {bid.bidAmount > auction.currentPrice ? 'Winning Bid' : 'Outbid'}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>
    );

  return (
    <div className="container mx-auto px-4 py-8">
      <Card className="max-w-2xl mx-auto">
        <CardHeader>
          <CardTitle className="text-2xl">{auction.itemName}</CardTitle>
          <ConnectionStatus />
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
              <CountdownTimer endTime={auction.endTime} />
            </div>
          </div>

          {auction.highestBidderUsername && (
            <div className="bg-primary/5 p-3 rounded-lg">
              <p className="text-sm text-gray-500">Current Highest Bidder</p>
              <p className="text-lg font-semibold text-primary">{auction.highestBidderUsername}</p>
            </div>
          )}

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

          <BidHistory />
        </CardContent>
      </Card>
    </div>
  );
}
