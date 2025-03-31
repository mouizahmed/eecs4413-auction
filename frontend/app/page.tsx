'use client';
import WelcomePage from '@/components/display/WelcomePage';
import AuctionList from '@/components/display/AuctionList';
import { useAuth } from '@/contexts/authContext';

export default function Home() {
  const { userLoggedIn } = useAuth();

  return userLoggedIn ? (
    <div className="container mx-auto p-6">
      <h1 className="text-2xl font-bold mb-6">Available Auctions</h1>
      <AuctionList />
    </div>
  ) : (
    <WelcomePage />
  );
}
