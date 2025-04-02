import { useState } from 'react';
import { Star } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import { toast } from 'sonner';

interface RatingFormProps {
  userId: string;
  onSubmit: (rating: number, feedback: string) => Promise<void>;
}

export function RatingForm({ userId, onSubmit }: RatingFormProps) {
  const [rating, setRating] = useState(0);
  const [feedback, setFeedback] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (rating === 0) {
      toast.error('Please select a rating');
      return;
    }
    if (!feedback.trim()) {
      toast.error('Please provide feedback');
      return;
    }

    setIsSubmitting(true);
    try {
      await onSubmit(rating, feedback);
      setRating(0);
      setFeedback('');
      toast.success('Rating submitted successfully');
    } catch (error) {
      toast.error('Failed to submit rating');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="flex items-center gap-2">
        {[1, 2, 3, 4, 5].map((star) => (
          <button key={star} type="button" onClick={() => setRating(star)} className="focus:outline-none">
            <Star size={24} className={star <= rating ? 'fill-current text-yellow-400' : 'text-gray-300'} />
          </button>
        ))}
      </div>
      <Textarea
        placeholder="Share your experience with this user..."
        value={feedback}
        onChange={(e) => setFeedback(e.target.value)}
        className="min-h-[100px]"
      />
      <Button type="submit" disabled={isSubmitting}>
        {isSubmitting ? 'Submitting...' : 'Submit Rating'}
      </Button>
    </form>
  );
}
