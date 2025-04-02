import { useEffect, useState } from 'react';
import { checkStatus } from '@/requests/postRequests';

interface CountdownTimerProps {
  endTime: string;
  itemId: string;
  onStatusUpdate: (updatedAuction: any) => void;
}

function CountdownTimer({ endTime, itemId, onStatusUpdate }: CountdownTimerProps) {
  const [timeLeft, setTimeLeft] = useState<string>('');
  const [hasCompleted, setHasCompleted] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const checkAuctionStatus = async () => {
    try {
      setError(null);
      const response = await checkStatus(itemId);
      onStatusUpdate(response);
    } catch (error) {
      setError('Failed to check auction status');
    }
  };

  useEffect(() => {
    const calculateTimeLeft = () => {
      try {
        setError(null);
        const end = new Date(endTime);
        if (isNaN(end.getTime())) {
          setTimeLeft('Invalid end time');
          return;
        }

        const now = new Date();
        const difference = end.getTime() - now.getTime();

        if (difference <= 0) {
          setTimeLeft('Auction ended');
          if (!hasCompleted) {
            setHasCompleted(true);
            checkAuctionStatus();
          }
          return;
        }

        const days = Math.floor(difference / (1000 * 60 * 60 * 24));
        const hours = Math.floor((difference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((difference % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((difference % (1000 * 60)) / 1000);

        setTimeLeft(`${days}d ${hours}h ${minutes}m ${seconds}s`);
      } catch (error) {
        setError('Error calculating time left');
        setTimeLeft('Error calculating time');
      }
    };

    if (!endTime) {
      setTimeLeft('No end time set');
      return;
    }

    calculateTimeLeft();
    const timer = setInterval(calculateTimeLeft, 1000);

    return () => clearInterval(timer);
  }, [endTime, itemId, hasCompleted, onStatusUpdate]);

  if (error) {
    return <p className="text-sm text-red-500">{error}</p>;
  }

  return <p className="text-sm text-gray-600">{timeLeft}</p>;
}

export { CountdownTimer };
