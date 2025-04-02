'use client';
import { useEffect, useState } from 'react';
import { useAuth } from '@/contexts/authContext';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { AuctionItem, ReceiptResponseDTO } from '@/types';
import { Button } from '@/components/ui/button';
import Link from 'next/link';
import { format } from 'date-fns';
import {
  getActiveBids,
  getUnpaidItems,
  getUserReceipts,
  getWonAuctions,
  getSellingItems,
} from '@/requests/getRequests';

export default function ProfilePage() {
  const { currentUser } = useAuth();
  const [wonAuctions, setWonAuctions] = useState<AuctionItem[]>([]);
  const [purchaseHistory, setPurchaseHistory] = useState<ReceiptResponseDTO[]>([]);
  const [unpaidItems, setUnpaidItems] = useState<AuctionItem[]>([]);
  const [currentBids, setCurrentBids] = useState<AuctionItem[]>([]);
  const [sellingItems, setSellingItems] = useState<AuctionItem[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        setError(null);
        // Fetch won auctions
        const wonResponse = await getWonAuctions();
        setWonAuctions(wonResponse);

        // Fetch purchase history
        const purchaseResponse = await getUserReceipts();
        setPurchaseHistory(purchaseResponse);

        // Fetch unpaid items
        const unpaidResponse = await getUnpaidItems();
        setUnpaidItems(unpaidResponse);

        // Fetch current bids
        const bidsResponse = await getActiveBids();
        setCurrentBids(bidsResponse);

        // Fetch selling items
        const sellingResponse = await getSellingItems();
        setSellingItems(sellingResponse);
      } catch (error) {
        setError((error as Error).message);
      } finally {
        setLoading(false);
      }
    };

    if (currentUser?.userID) {
      fetchUserData();
    }
  }, [currentUser?.userID]);

  if (loading) return <div className="container mx-auto py-8">Loading profile data...</div>;
  if (error) return <div className="container mx-auto py-8 text-red-500">Error: {error}</div>;

  const AuctionCard = ({ item }: { item: AuctionItem }) => (
    <Card className="mb-4">
      <CardHeader>
        <CardTitle>{item.itemName}</CardTitle>
        {item.auctionType === 'FORWARD' && (
          <CardDescription>End Time: {format(new Date(item.endTime), 'PPP p')}</CardDescription>
        )}
      </CardHeader>
      <CardContent>
        <div className="flex justify-between items-center">
          <div>
            <p className="text-sm text-muted-foreground">Current Price: ${item.currentPrice}</p>
            <p className="text-sm text-muted-foreground">Seller: {item.sellerUsername}</p>
          </div>
          <Link href={`/auction/${item.itemID}`}>
            <Button variant="outline">View Details</Button>
          </Link>
        </div>
      </CardContent>
    </Card>
  );

  const PurchaseCard = ({ purchase }: { purchase: ReceiptResponseDTO }) => (
    <Card className="mb-4">
      <CardHeader>
        <CardTitle>Purchase #{purchase.receiptID}</CardTitle>
        <CardDescription>Date: {format(new Date(purchase.timestamp), 'PPP p')}</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="flex justify-between items-center">
          <div>
            <p className="text-sm text-muted-foreground">Total Cost: ${purchase.totalCost}</p>
            <p className="text-sm text-muted-foreground">Shipping Time: {purchase.shippingTime} days</p>
          </div>
          <Link href={`/receipt/${purchase.receiptID}`}>
            <Button variant="outline">View Receipt</Button>
          </Link>
        </div>
      </CardContent>
    </Card>
  );

  return (
    <div className="container mx-auto py-8">
      <h1 className="text-3xl font-bold mb-8">Profile</h1>
      <div className="mb-8">
        <h2 className="text-xl font-semibold mb-2">Welcome, {currentUser?.username}!</h2>
        <p className="text-muted-foreground">
          {currentUser?.firstName} {currentUser?.lastName}
        </p>
      </div>

      {unpaidItems.length > 0 && (
        <div className="mb-8">
          <h3 className="text-xl font-semibold mb-4 text-red-600">Unpaid Items</h3>
          {unpaidItems.map((item) => (
            <AuctionCard key={item.itemID} item={item} />
          ))}
        </div>
      )}

      <Tabs defaultValue="current-bids" className="w-full">
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="current-bids">Current Bids</TabsTrigger>
          <TabsTrigger value="won-auctions">Won Auctions</TabsTrigger>
          <TabsTrigger value="purchase-history">Purchase History</TabsTrigger>
          <TabsTrigger value="selling">Selling</TabsTrigger>
        </TabsList>

        <TabsContent value="current-bids">
          <h3 className="text-xl font-semibold mb-4">Currently Bidding On</h3>
          {currentBids.length === 0 ? (
            <p className="text-muted-foreground">No active bids</p>
          ) : (
            currentBids.map((item) => <AuctionCard key={item.itemID} item={item} />)
          )}
        </TabsContent>

        <TabsContent value="won-auctions">
          <h3 className="text-xl font-semibold mb-4">Auctions Won</h3>
          {wonAuctions.length === 0 ? (
            <p className="text-muted-foreground">No won auctions</p>
          ) : (
            wonAuctions.map((item) => <AuctionCard key={item.itemID} item={item} />)
          )}
        </TabsContent>

        <TabsContent value="purchase-history">
          <h3 className="text-xl font-semibold mb-4">Purchase History</h3>
          {purchaseHistory.length === 0 ? (
            <p className="text-muted-foreground">No purchase history</p>
          ) : (
            purchaseHistory.map((purchase) => <PurchaseCard key={purchase.receiptID} purchase={purchase} />)
          )}
        </TabsContent>

        <TabsContent value="selling">
          <h3 className="text-xl font-semibold mb-4">Items You're Selling</h3>
          {sellingItems.length === 0 ? (
            <p className="text-muted-foreground">No items being sold</p>
          ) : (
            sellingItems.map((item) => <AuctionCard key={item.itemID} item={item} />)
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}
