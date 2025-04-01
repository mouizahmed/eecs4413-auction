'use client';
import WelcomePage from '@/components/display/WelcomePage';
import AuctionList from '@/components/display/AuctionList';
import { useAuth } from '@/contexts/authContext';
import Link from 'next/link';

export default function Home() {
  const { userLoggedIn } = useAuth();

  return userLoggedIn ? (
    <div className="container mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Available Auctions</h1>
        <Link
          href="/post-auction"
          className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors"
        >
          Post New Auction
        </Link>
      </div>
      <AuctionList />
    </div>
  ) : (
    <WelcomePage />
  );
}
