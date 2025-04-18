'use client';
import React, { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { CountdownTimer } from '@/components/ui/countdown';
import { AuctionItem } from '@/types';
import { placeBid } from '@/requests/postRequests';
import { useAuth } from '@/contexts/authContext';
import { useAuctionWebSocket } from '@/hooks/useAuctionWebsocket';
import { BidHistory } from '@/components/display/BidHistory';
import { getAuctionDetails } from '@/requests/getRequests';
import { decreasePrice } from '@/requests/patchRequests';
import { SellerRating } from '@/components/display/SellerRating';
import Link from 'next/link';

export default function Auctionpage() {
  const params = useParams();
  const router = useRouter();
  const { currentUser } = useAuth();
  const [auction, setAuction] = useState<AuctionItem | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [bidAmount, setBidAmount] = useState<number>(0);
  const [decreaseAmount, setDecreaseAmount] = useState<string>('');
  const [isDecreasing, setIsDecreasing] = useState(false);
  const { lastMessage, socketStatus, isSubscribed } = useAuctionWebSocket(String(params.id));

  // Handle WebSocket messages
  useEffect(() => {
    if (!lastMessage) return;

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
  }, [lastMessage]);

  useEffect(() => {
    if (!lastMessage) return;

    if (lastMessage.type === 'BID_PLACED') {
      console.log('NEW BID');
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
  }, [lastMessage]);

  useEffect(() => {
    const fetchAuction = async () => {
      try {
        const auctionItem: AuctionItem = await getAuctionDetails(String(params.id));
        setAuction(auctionItem);
        if (auctionItem.auctionType.toLowerCase() == 'dutch') {
          setBidAmount(auctionItem.currentPrice);
        } else {
          setBidAmount(auctionItem.currentPrice + 1);
        }
      } catch (error) {
        setError((error as Error).message);
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
      setBidAmount(bidAmount + 1);
    } catch (error) {
      setError((error as Error).message);
    }
  };

  const handlePay = async () => {
    if (!auction) return;
    router.push(`/payment/${auction.itemID}`);
  };

  const handleDecreasePrice = async () => {
    if (!auction || !decreaseAmount) return;
    const amount = Number(decreaseAmount);
    if (amount <= 0) return;
    try {
      setIsDecreasing(true);
      await decreasePrice(auction.itemID, amount);
    } catch (error) {
      setError((error as Error).message);
    } finally {
      setIsDecreasing(false);
    }
  };

  const isHighestBidder = currentUser && auction?.highestBidderUsername === currentUser.username;
  const isAuctionEnded = auction?.auctionStatus === 'SOLD';
  const canPay = isHighestBidder && isAuctionEnded && auction?.auctionStatus !== 'PAID';
  const isSeller = currentUser && auction?.sellerUsername === currentUser.username;
  const canBid = !isHighestBidder && auction?.auctionStatus === 'AVAILABLE';

  if (loading) return <div>Loading auction details...</div>;
  if (error) return <div className="text-red-500">{error}</div>;
  if (!auction) return <div>Auction not found</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <Card className="max-w-2xl mx-auto">
        <CardHeader>
          <CardTitle className="text-2xl">
            {auction.itemName}
            <p className="text-sm text-muted-foreground">{auction.auctionType}</p>
          </CardTitle>
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
          <div className={`grid gap-4 ${isSeller && auction.auctionType === 'DUTCH' ? 'grid-cols-3' : 'grid-cols-2'}`}>
            <div>
              <p className="text-sm text-gray-500">Current Price</p>
              <p className="text-xl font-semibold">${auction.currentPrice}</p>
            </div>
            {isSeller && auction.auctionType === 'DUTCH' && (
              <div>
                <p className="text-sm text-gray-500">Reserve Price</p>
                <p className="text-xl font-semibold">${auction.reservePrice}</p>
              </div>
            )}
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
            {auction.auctionType !== 'DUTCH' && (
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
            )}
          </div>

          <div className="bg-primary/5 p-4 rounded-lg">
            <div>
              <p className="text-sm text-gray-500">Seller</p>
              <div className="flex items-center gap-2">
                <Link
                  href={`/ratings/${auction.sellerUsername}`}
                  className="text-xl font-semibold hover:text-primary transition-colors"
                >
                  {auction.sellerUsername}
                </Link>
                <SellerRating sellerId={auction.sellerID} sellerUsername={auction.sellerUsername} />
              </div>
            </div>
          </div>

          {auction.highestBidderUsername && (
            <div className="bg-primary/5 p-3 rounded-lg">
              <p className="text-sm text-gray-500">
                {auction.auctionStatus === 'AVAILABLE' ? 'Highest Bidder' : 'Winner'}
              </p>
              <p className="text-xl font-semibold">{auction.highestBidderUsername}</p>
            </div>
          )}

          {!isSeller && auction.auctionStatus === 'AVAILABLE' && (
            <div className="space-y-2">
              {auction.auctionType === 'DUTCH' ? (
                <Button onClick={handleBid} className="w-full" disabled={!canBid}>
                  Buy Now
                </Button>
              ) : (
                <>
                  <Input
                    type="number"
                    placeholder="Enter bid amount"
                    value={bidAmount}
                    onChange={(e) => setBidAmount(Number(e.target.value))}
                    min={auction.currentPrice}
                    step="1"
                    disabled={!canBid}
                  />
                  <Button onClick={handleBid} className="w-full mt-2" disabled={!canBid}>
                    Place Bid
                  </Button>
                </>
              )}
            </div>
          )}

          {!isSeller && canPay && (
            <Button onClick={handlePay} className="w-full">
              Pay Now
            </Button>
          )}

          <div className="mt-8">
            <BidHistory bids={auction.bids} currentPrice={auction.currentPrice} />
          </div>

          {isSeller && auction.auctionType === 'DUTCH' && auction.auctionStatus === 'AVAILABLE' && (
            <div className="space-y-2 pt-4 border-t">
              <p className="text-sm text-gray-500">Decrease Price</p>
              <div className="flex gap-2">
                <Input
                  type="number"
                  placeholder="Enter decrease amount"
                  value={decreaseAmount}
                  onChange={(e) => setDecreaseAmount(e.target.value)}
                  min="0"
                  step="1"
                  className="flex-1"
                />
                <Button
                  onClick={handleDecreasePrice}
                  disabled={isDecreasing || !decreaseAmount || Number(decreaseAmount) <= 0}
                >
                  {isDecreasing ? 'Decreasing...' : 'Decrease'}
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
