'use client';
import React, { useEffect, useState, useCallback, memo } from 'react';
import { useParams } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { CountdownTimer } from '@/components/ui/countdown';
import { AuctionItem, ConnectionStatus, WebSocketMessage } from '@/types';
import axios from 'axios';
import { placeBid } from '@/requests/postRequests';
import { webSocketService } from '@/services/websocket';
import { useAuth } from '@/contexts/authContext';

const ConnectionStatusComponent = memo(
  ({ status, itemId, isSubscribed }: { status: ConnectionStatus; itemId: string; isSubscribed: boolean }) => (
    <div className="flex flex-col gap-2 text-sm">
      <div className="flex items-center gap-2">
        <div
          className={`w-2 h-2 rounded-full ${
            status === 'connected'
              ? 'bg-green-500'
              : status === 'connecting'
              ? 'bg-yellow-500'
              : status === 'error'
              ? 'bg-red-500'
              : 'bg-gray-500'
          }`}
        />
        <span className="text-gray-600">
          {status === 'connected'
            ? 'WebSocket Connected'
            : status === 'connecting'
            ? 'Connecting...'
            : status === 'error'
            ? 'Connection Error'
            : 'Disconnected - Retrying...'}
        </span>
      </div>
      {status === 'connected' && (
        <div className="flex items-center gap-2">
          <div className={`w-2 h-2 rounded-full ${isSubscribed ? 'bg-green-500' : 'bg-yellow-500'}`} />
          <span className={isSubscribed ? 'text-green-600' : 'text-yellow-600'}>
            {isSubscribed ? `Subscribed to updates for item` : 'Waiting for subscription confirmation...'}
          </span>
        </div>
      )}
    </div>
  )
);

const BidHistory = memo(({ bids, currentPrice }: { bids: AuctionItem['bids']; currentPrice: number }) => {
  if (!bids || bids.length === 0) return null;

  return (
    <div className="pt-4 border-t">
      <div className="flex justify-between items-center mb-4">
        <h3 className="text-lg font-semibold">Bid History</h3>
        <span className="text-sm text-gray-500">{bids.length} bids</span>
      </div>
      <div className="space-y-2">
        {bids.map((bid) => (
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
              <p className="text-xs text-gray-500">{bid.bidAmount > currentPrice ? 'Winning Bid' : 'Outbid'}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
});

export default function ForwardAuctionPage() {
  const params = useParams();
  const { currentUser } = useAuth();
  const [auction, setAuction] = useState<AuctionItem | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [bidAmount, setBidAmount] = useState<string>('');
  const [wsStatus, setWsStatus] = useState<ConnectionStatus>('connecting');
  const [isSubscribed, setIsSubscribed] = useState(false);
  const [isPaying, setIsPaying] = useState(false);

  const handleWebSocketMessage = useCallback(
    (message: WebSocketMessage) => {
      if (message.type === 'SUBSCRIBED' && message.itemId === params.id) {
        setIsSubscribed(true);
        return;
      }

      if (message.type === 'ERROR') {
        console.error('WebSocket error:', message);
        setIsSubscribed(false);
        return;
      }

      setAuction((prevAuction) => {
        if (!prevAuction) return prevAuction;

        if (message.type === 'BID_PLACED' && message.bidId && message.userId && message.bidAmount && message.username) {
          const newBid = {
            bidID: message.bidId,
            itemID: message.itemId,
            userID: message.userId,
            username: message.username,
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
    },
    [params.id]
  );

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

    fetchAuction();
    setIsSubscribed(false);
    const unsubStatus = webSocketService.subscribeToStatus(setWsStatus);
    const unsubAuction = webSocketService.subscribe(params.id as string, handleWebSocketMessage);

    return () => {
      unsubStatus();
      unsubAuction();
      setIsSubscribed(false);
    };
  }, [params.id, handleWebSocketMessage]);

  const handleBid = async () => {
    if (!auction || !bidAmount) return;
    try {
      await placeBid(auction.itemID, Number(bidAmount));
      setBidAmount('');
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
          <ConnectionStatusComponent status={wsStatus} itemId={params.id as string} isSubscribed={isSubscribed} />
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
                onStatusUpdate={(updatedAuction) => setAuction(updatedAuction)}
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
