import { useEffect, useState } from 'react';

function CountdownTimer({ endTime }: { endTime: string }) {
  const [timeLeft, setTimeLeft] = useState<string>('');

  useEffect(() => {
    const calculateTimeLeft = () => {
      try {
        const end = new Date(endTime);
        if (isNaN(end.getTime())) {
          setTimeLeft('Invalid end time');
          return;
        }

        const now = new Date();
        const difference = end.getTime() - now.getTime();

        if (difference <= 0) {
          setTimeLeft('Auction ended');
          return;
        }

        const days = Math.floor(difference / (1000 * 60 * 60 * 24));
        const hours = Math.floor((difference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((difference % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((difference % (1000 * 60)) / 1000);

        setTimeLeft(`${days}d ${hours}h ${minutes}m ${seconds}s`);
      } catch (error) {
        console.error('Error calculating time left:', error);
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
  }, [endTime]);

  return <p className="text-sm text-gray-600">Time left: {timeLeft}</p>;
}

export { CountdownTimer };
