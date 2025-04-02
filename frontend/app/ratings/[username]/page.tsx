'use client';
import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { StarRating } from '@/components/ui/star-rating';
import { getUserRatings, getUserAverageRating, findByUsername } from '@/requests/getRequests';

interface Rating {
  ratingID: string;
  rating: number;
  feedback: string;
  timestamp: string;
  raterUser: {
    username: string;
  };
}

export default function RatingsPage() {
  const params = useParams();
  const [ratings, setRatings] = useState<Rating[]>([]);
  const [averageRating, setAverageRating] = useState(0);
  const [username, setUsername] = useState<string>('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const username = String(params.username);
        setUsername(username);

        // First get the user's ID from their username
        const user = await findByUsername(username);

        const [ratingsData, avgData] = await Promise.all([
          getUserRatings(user.userID),
          getUserAverageRating(user.userID),
        ]);
        setRatings(ratingsData);
        setAverageRating(avgData);
      } catch (error) {
        setError((error as Error).message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [params.username]);

  if (loading) return <div>Loading ratings...</div>;
  if (error) return <div className="text-red-500">{error}</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <Card className="max-w-3xl mx-auto">
        <CardHeader>
          <CardTitle className="text-2xl text-center">{username}'s Ratings</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-6">
            <div className="flex items-center justify-center gap-4 p-6 bg-primary/5 rounded-lg">
              <div className="text-4xl font-bold">{averageRating.toFixed(1)}</div>
              <StarRating rating={averageRating} size={32} />
              <div className="text-lg text-gray-500">
                {ratings.length} {ratings.length === 1 ? 'rating' : 'ratings'}
              </div>
            </div>

            <div className="space-y-4">
              {ratings.length === 0 ? (
                <p className="text-center text-gray-500">No ratings yet</p>
              ) : (
                ratings.map((rating) => (
                  <Card key={rating.ratingID} className="p-4">
                    <div className="flex items-start justify-between">
                      <div className="space-y-2">
                        <div className="flex items-center gap-2">
                          <StarRating rating={rating.rating} size={20} />
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
        </CardContent>
      </Card>
    </div>
  );
}
