import { useEffect, useState } from 'react';
import { Card } from '@/components/ui/card';
import { StarRating } from '../ui/star-rating';
import { RatingForm } from './RatingForm';
import { getUserRatings, getUserAverageRating } from '@/requests/getRequests';
import { createRating } from '@/requests/postRequests';
import Link from 'next/link';

interface Rating {
  ratingID: string;
  rating: number;
  feedback: string;
  timestamp: string;
  raterUser: {
    username: string;
  };
}

interface RatingDisplayProps {
  userId: string;
  currentUserId: string;
  username: string;
}

export function RatingDisplay({ userId, currentUserId, username }: RatingDisplayProps) {
  const [ratings, setRatings] = useState<Rating[]>([]);
  const [averageRating, setAverageRating] = useState(0);
  const [isLoading, setIsLoading] = useState(true);

  const fetchRatings = async () => {
    try {
      const [ratingsData, avgData] = await Promise.all([getUserRatings(userId), getUserAverageRating(userId)]);
      setRatings(ratingsData);
      setAverageRating(avgData);
    } catch (error) {
      console.error('Failed to load ratings:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchRatings();
  }, [userId]);

  const handleSubmitRating = async (rating: number, feedback: string) => {
    try {
      await createRating(userId, rating, feedback);
      await fetchRatings();
    } catch (error) {
      throw error;
    }
  };

  if (isLoading) {
    return <div>Loading ratings...</div>;
  }

  return (
    <div className="space-y-6">
      <Link href={`/ratings/${username}`}>
        <Card className="p-4 hover:bg-primary/5 transition-colors cursor-pointer">
          <div className="flex items-center gap-4">
            <div className="text-2xl font-bold">{averageRating.toFixed(1)}</div>
            <StarRating rating={averageRating} size={24} />
            <div className="text-gray-500">
              {ratings.length} {ratings.length === 1 ? 'rating' : 'ratings'}
            </div>
          </div>
        </Card>
      </Link>

      {currentUserId !== userId && (
        <Card className="p-4">
          <h3 className="text-lg font-semibold mb-4">Rate {username}</h3>
          <RatingForm userId={userId} onSubmit={handleSubmitRating} />
        </Card>
      )}

      <div className="space-y-4">
        <div className="flex justify-between items-center">
          <h3 className="text-lg font-semibold">Recent Ratings</h3>
          <Link href={`/ratings/${username}`} className="text-sm text-primary hover:underline">
            View all
          </Link>
        </div>
        {ratings.length === 0 ? (
          <p className="text-gray-500">No ratings yet</p>
        ) : (
          ratings.slice(0, 3).map((rating) => (
            <Card key={rating.ratingID} className="p-4">
              <div className="flex items-start justify-between">
                <div className="space-y-2">
                  <div className="flex items-center gap-2">
                    <StarRating rating={rating.rating} size={16} />
                    <span className="text-sm text-gray-500">by {rating.raterUser.username}</span>
                  </div>
                  <p className="text-sm">{rating.feedback}</p>
                  <p className="text-xs text-gray-500">{new Date(rating.timestamp).toLocaleDateString()}</p>
                </div>
              </div>
            </Card>
          ))
        )}
      </div>
    </div>
  );
}
