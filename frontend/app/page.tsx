'use client';
import WelcomePage from '@/components/display/WelcomePage';
import AuctionList from '@/components/display/AuctionList';
import { useAuth } from '@/contexts/authContext';
import Link from 'next/link';
import { Button } from '@/components/ui/button';

export default function Home() {
  const { userLoggedIn } = useAuth();

  return userLoggedIn ? (
    <div className="container mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Available Auctions</h1>
        <Link href="/post-auction">
          <Button className="cursor-pointer">Post New Auction</Button>
        </Link>
      </div>
      <div className="min-h-[calc(100vh-200px)]">
        <AuctionList />
      </div>
    </div>
  ) : (
    <WelcomePage />
  );
}
