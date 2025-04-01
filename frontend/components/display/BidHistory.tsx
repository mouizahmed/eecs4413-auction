import { AuctionItem } from '@/types';

export const BidHistory = ({ bids, currentPrice }: { bids: AuctionItem['bids']; currentPrice: number }) => {
  if (!bids || bids.length === 0) return null;

  return (
    <div className="pt-4 border-t">
      <div className="flex justify-between items-center mb-4">
        <h3 className="text-lg font-semibold">Bid History</h3>
        <span className="text-sm text-gray-500">{bids.length} bids</span>
      </div>
      <div className="space-y-2">
        {bids.map((bid) => (
          <div
            key={`${bid.bidID}`}
            className="flex justify-between items-center p-3 bg-white border rounded-lg shadow-sm hover:shadow-md transition-shadow"
          >
            <div>
              <p className="font-medium text-gray-900">{bid.username}</p>
              <p className="text-sm text-gray-500">{new Date(bid.timestamp).toLocaleString()}</p>
            </div>
            <div className="text-right">
              <p className="font-semibold text-lg text-primary">${bid.bidAmount}</p>
              <p className="text-xs text-gray-500">{bid.bidAmount === currentPrice ? 'Winning Bid' : 'Outbid'}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
