import { Star } from 'lucide-react';

interface StarRatingProps {
  rating: number;
  size?: number;
  color?: string;
}

export function StarRating({ rating, size = 20, color = '#FFD700' }: StarRatingProps) {
  return (
    <div className="flex items-center gap-1">
      {[1, 2, 3, 4, 5].map((star) => (
        <Star
          key={star}
          size={size}
          className={star <= rating ? 'fill-current' : 'text-gray-300'}
          color={star <= rating ? color : '#D1D5DB'}
        />
      ))}
    </div>
  );
}
