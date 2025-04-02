'use client';
import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useAuth } from '@/contexts/authContext';
import { ReceiptResponseDTO } from '@/types';
import { getReceiptDetails } from '@/requests/getRequests';

export default function ReceiptPage() {
  const params = useParams();
  const { currentUser } = useAuth();
  const [receipt, setReceipt] = useState<ReceiptResponseDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchReceipt = async () => {
      try {
        const receiptData = await getReceiptDetails(String(params.id));
        setReceipt(receiptData);
      } catch (error) {
        setError((error as Error).message);
      } finally {
        setLoading(false);
      }
    };

    fetchReceipt();
  }, [params.id]);

  if (loading) return <div>Loading receipt details...</div>;
  if (error) return <div className="text-red-500">{error}</div>;
  if (!receipt) return <div>Receipt not found</div>;

  // Check if current user is authorized to view this receipt
  if (currentUser?.userID !== receipt.userID) {
    return (
      <div className="container mx-auto px-4 py-8">
        <Card className="max-w-3xl mx-auto">
          <CardHeader>
            <CardTitle className="text-2xl text-center text-red-600">Access Denied</CardTitle>
          </CardHeader>
          <CardContent className="text-center">
            <p className="text-lg mb-4">You are not authorized to view this receipt.</p>
            <p className="text-gray-600">Only the receipt owner can view the receipt details.</p>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <Card className="max-w-3xl mx-auto">
        <CardHeader>
          <CardTitle className="text-2xl text-center">Payment Receipt</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-6">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <h3 className="font-semibold text-gray-600">Receipt ID</h3>
                <p>{receipt.receiptID}</p>
              </div>
              <div>
                <h3 className="font-semibold text-gray-600">Date</h3>
                <p>{new Date(receipt.timestamp).toLocaleString()}</p>
              </div>
              <div>
                <h3 className="font-semibold text-gray-600">Item ID</h3>
                <p>{receipt.itemID}</p>
              </div>
              <div>
                <h3 className="font-semibold text-gray-600">Total Cost</h3>
                <p className="text-xl font-bold">${receipt.totalCost.toFixed(2)}</p>
              </div>
              <div>
                <h3 className="font-semibold text-gray-600">Shipping Time</h3>
                <p>{receipt.shippingTime} days</p>
              </div>
            </div>

            <div className="border-t pt-6">
              <h3 className="text-lg font-semibold mb-4">Shipping Address</h3>
              <div className="space-y-2">
                <p>
                  {receipt.address.streetNum} {receipt.address.streetName}
                </p>
                <p>
                  {receipt.address.city}, {receipt.address.province}
                </p>
                <p>{receipt.address.postalCode}</p>
                <p>{receipt.address.country}</p>
              </div>
            </div>

            <div className="border-t pt-6">
              <h3 className="text-lg font-semibold mb-4">Payment Details</h3>
              <div className="space-y-2">
                <p>Card ending in: {receipt.creditCard.cardNum.slice(-4)}</p>
                <p>Cardholder: {receipt.creditCard.cardName}</p>
                <p>Expires: {receipt.creditCard.expDate.toString()}</p>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
