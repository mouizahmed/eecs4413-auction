import { useEffect, useState } from 'react';
import { StarRating } from '../ui/star-rating';
import { getUserAverageRating, getUserTotalRatings } from '@/requests/getRequests';
import Link from 'next/link';

interface SellerRatingProps {
  sellerId: string;
  sellerUsername: string;
}

export function SellerRating({ sellerId, sellerUsername }: SellerRatingProps) {
  const [averageRating, setAverageRating] = useState(0);
  const [totalRatings, setTotalRatings] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchRating = async () => {
      try {
        const [avgRating, total] = await Promise.all([getUserAverageRating(sellerId), getUserTotalRatings(sellerId)]);
        setAverageRating(avgRating);
        setTotalRatings(total);
      } catch (error) {
        console.error('Failed to fetch rating:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchRating();
  }, [sellerId]);

  if (loading) return null;

  return (
    <Link href={`/ratings/${sellerUsername}`} className="inline-flex items-center gap-2 hover:opacity-80">
      <StarRating rating={averageRating} size={16} />
      <span className="text-sm text-gray-500">
        ({totalRatings} {totalRatings === 1 ? 'rating' : 'ratings'})
      </span>
    </Link>
  );
}
