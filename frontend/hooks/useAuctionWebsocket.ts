'use client';
import { useEffect, useRef, useState, useCallback } from 'react';

export const useAuctionWebSocket = (itemID: string) => {
  const wsRef = useRef<WebSocket | null>(null);
  const reconnectTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const [lastMessage, setLastMessage] = useState<any>(null);
  const [socketStatus, setSocketStatus] = useState<'OPEN' | 'CLOSED' | 'CONNECTING'>('CONNECTING');
  const [retryCount, setRetryCount] = useState(0);
  const [isSubscribed, setIsSubscribed] = useState(false);

  const MAX_RETRIES = 5;
  const BASE_DELAY = 500;

  const connect = useCallback(() => {
    if (retryCount >= MAX_RETRIES) {
      console.log('Max retry attempts reached, giving up');
      setSocketStatus('CLOSED');
      return;
    }

    const delay = retryCount === 0 ? 0 : BASE_DELAY * Math.pow(2, retryCount - 1);

    if (retryCount > 0) {
      console.log(`Attempting to reconnect in ${delay}ms (try ${retryCount}/${MAX_RETRIES})...`);
    }

    // Clear any existing connection
    if (wsRef.current) {
      try {
        wsRef.current.close();
      } catch (error) {
        // Ignore errors when closing
      }
      wsRef.current = null;
    }

    // Set a timeout for the next connection attempt
    reconnectTimeoutRef.current = setTimeout(() => {
      setSocketStatus('CONNECTING');

      try {
        const ws = new WebSocket('ws://localhost:8080/ws/auction-updates');
        wsRef.current = ws;

        ws.onopen = () => {
          console.log('WebSocket connection established');
          setSocketStatus('OPEN');
          setRetryCount(0);

          // Verify the connection is actually open
          if (ws.readyState === WebSocket.OPEN) {
            // Send the itemID to subscribe to specific auction updates
            try {
              ws.send(
                JSON.stringify({
                  type: 'SUBSCRIBE',
                  itemId: itemID,
                })
              );
            } catch (error) {
              console.log('Error sending subscribe message:', error);
              setSocketStatus('CLOSED');
            }
          } else {
            console.log('WebSocket connection not actually open');
            setSocketStatus('CLOSED');
          }
        };

        ws.onmessage = (event) => {
          try {
            const data = JSON.parse(event.data);
            console.log('WebSocket message received:', data);
            setLastMessage(data);

            if (data.type === 'SUBSCRIBED') {
              setIsSubscribed(true);
            }
          } catch (error) {
            console.log('Error parsing WebSocket message:', error);
          }
        };

        ws.onerror = (error) => {
          console.log('WebSocket error:', error);
          setSocketStatus('CLOSED');
        };

        ws.onclose = (event) => {
          console.log(`WebSocket connection closed: ${event.code} ${event.reason}`);
          wsRef.current = null;
          setSocketStatus('CLOSED');
          setIsSubscribed(false);

          if (retryCount < MAX_RETRIES) {
            setRetryCount((prev) => prev + 1);
            connect();
          }
        };
      } catch (error) {
        console.log('Error creating WebSocket connection:', error);
        setSocketStatus('CLOSED');

        if (retryCount < MAX_RETRIES) {
          setRetryCount((prev) => prev + 1);
          connect();
        }
      }
    }, delay);
  }, [itemID, retryCount]);

  // Initialize connection
  useEffect(() => {
    connect();

    // Clean up function
    return () => {
      console.log('Cleaning up WebSocket connection');

      if (reconnectTimeoutRef.current) {
        clearTimeout(reconnectTimeoutRef.current);
        reconnectTimeoutRef.current = null;
      }

      if (wsRef.current) {
        const ws = wsRef.current;

        ws.onopen = null;
        ws.onmessage = null;
        ws.onerror = null;
        ws.onclose = null;

        try {
          if (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING) {
            ws.close(1000, 'Component unmounting');
          }
        } catch (error) {
          console.log('Error closing WebSocket:', error);
        }

        wsRef.current = null;
      }
    };
  }, [connect]);

  const reconnect = useCallback(() => {
    setRetryCount(0);
    connect();
  }, [connect]);

  return { lastMessage, socketStatus, reconnect, isSubscribed };
};
