'use client';
import { useEffect, useRef, useState } from 'react';

export const useAuctionWebSocket = (itemID: string) => {
  const wsRef = useRef<WebSocket | null>(null);
  const [lastMessage, setLastMessage] = useState<any>(null);
  const [socketStatus, setSocketStatus] = useState<'OPEN' | 'CLOSED' | 'CONNECTING'>('CONNECTING');

  useEffect(() => {
    // Create WebSocket connection
    const ws = new WebSocket('ws://localhost:8080/ws/auction-updates');
    wsRef.current = ws;

    // Connection opened
    ws.onopen = () => {
      console.log('WebSocket connection established');
      setSocketStatus('OPEN');
      // Send the itemID to subscribe to specific auction updates
      ws.send(
        JSON.stringify({
          type: 'SUBSCRIBE',
          itemId: itemID,
        })
      );
    };

    // Listen for messages
    ws.onmessage = (event) => {
      const data = JSON.parse(event.data);
      console.log('WebSocket message received:', data);
      setLastMessage(data);
    };

    // Listen for errors
    ws.onerror = (error) => {
      console.error('WebSocket error:', error);
      setSocketStatus('CLOSED');
    };

    // Listen for connection close
    ws.onclose = () => {
      console.log('WebSocket connection closed');
      setSocketStatus('CLOSED');
    };

    // Clean up function - modified to prevent errors
    return () => {
      console.log('Cleaning up WebSocket connection');

      // Only close if connection is open or connecting
      if (
        wsRef.current &&
        (wsRef.current.readyState === WebSocket.OPEN || wsRef.current.readyState === WebSocket.CONNECTING)
      ) {
        // Remove all event listeners before closing
        wsRef.current.onopen = null;
        wsRef.current.onmessage = null;
        wsRef.current.onerror = null;
        wsRef.current.onclose = null;

        // Close with code 1000 (normal closure)
        wsRef.current.close(1000, 'Component unmounting');
      }

      // Clear the reference
      wsRef.current = null;
    };
  }, [itemID]);

  return { lastMessage, socketStatus };
};
