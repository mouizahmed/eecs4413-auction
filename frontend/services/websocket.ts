import { ConnectionStatus, WebSocketMessage } from '@/types';

class WebSocketService {
  private socket: WebSocket | null = null;
  private subscribers: Map<string, ((data: WebSocketMessage) => void)[]> = new Map();
  private statusSubscribers: ((status: ConnectionStatus) => void)[] = [];
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectTimeout: NodeJS.Timeout | null = null;
  private activeSubscriptions = 0;
  private isIntentionalClose = false;

  private connect() {
    if (this.socket?.readyState === WebSocket.OPEN || this.socket?.readyState === WebSocket.CONNECTING) {
      return;
    }

    this.notifyStatusChange('connecting');
    this.isIntentionalClose = false;

    try {
      this.socket = new WebSocket('ws://localhost:8080/ws/auction-updates');

      this.socket.onopen = () => {
        console.log('WebSocket connected successfully');
        this.notifyStatusChange('connected');
        this.reconnectAttempts = 0;
        this.subscribers.forEach((_, itemId) => this.sendSubscription(itemId));
      };

      this.socket.onmessage = (event) => {
        try {
          const message: WebSocketMessage = JSON.parse(event.data);
          if (message.itemId) {
            this.subscribers.get(message.itemId)?.forEach((cb) => cb(message));
          }
        } catch (error) {
          console.error('Error handling WebSocket message:', error);
        }
      };

      this.socket.onerror = (error) => {
        console.error('WebSocket error:', error);
        if (!this.isIntentionalClose) {
          this.notifyStatusChange('error');
          this.handleReconnect();
        }
      };

      this.socket.onclose = (event) => {
        console.log('WebSocket closed:', event.code, event.reason);
        this.socket = null;

        if (!this.isIntentionalClose) {
          this.notifyStatusChange('disconnected');
          this.handleReconnect();
        } else {
          this.notifyStatusChange('disconnected');
        }
      };
    } catch (error) {
      console.error('Error creating WebSocket connection:', error);
      if (!this.isIntentionalClose) {
        this.notifyStatusChange('error');
        this.handleReconnect();
      }
    }
  }

  private handleReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      this.notifyStatusChange('error');
      return;
    }

    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
    }

    this.reconnectAttempts++;
    const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000); // Exponential backoff, max 30 seconds
    this.reconnectTimeout = setTimeout(() => this.connect(), delay);
  }

  private notifyStatusChange(status: ConnectionStatus) {
    this.statusSubscribers.forEach((cb) => cb(status));
  }

  private sendSubscription(itemId: string) {
    if (this.socket?.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify({ type: 'SUBSCRIBE', itemId, itemName: '', currentPrice: 0 }));
    }
  }

  subscribe(itemId: string, callback: (data: WebSocketMessage) => void) {
    if (!this.subscribers.has(itemId)) {
      this.subscribers.set(itemId, []);
    }
    this.subscribers.get(itemId)?.push(callback);
    this.activeSubscriptions++;

    if (!this.socket || this.socket.readyState !== WebSocket.OPEN) {
      this.connect();
    } else {
      this.sendSubscription(itemId);
    }

    return () => {
      const callbacks = this.subscribers.get(itemId) || [];
      this.subscribers.set(
        itemId,
        callbacks.filter((cb) => cb !== callback)
      );
      this.activeSubscriptions--;

      // If this was the last subscription for this item, remove the item entry
      if (this.subscribers.get(itemId)?.length === 0) {
        this.subscribers.delete(itemId);
      }

      // If there are no more active subscriptions, disconnect
      if (this.activeSubscriptions === 0) {
        this.disconnect();
      }
    };
  }

  subscribeToStatus(callback: (status: ConnectionStatus) => void) {
    this.statusSubscribers.push(callback);
    this.activeSubscriptions++;

    return () => {
      this.statusSubscribers = this.statusSubscribers.filter((cb) => cb !== callback);
      this.activeSubscriptions--;

      // If there are no more active subscriptions, disconnect
      if (this.activeSubscriptions === 0) {
        this.disconnect();
      }
    };
  }

  disconnect() {
    this.isIntentionalClose = true;

    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
      this.reconnectTimeout = null;
    }

    if (this.socket) {
      // Only try to close if the socket is still open
      if (this.socket.readyState === WebSocket.OPEN) {
        this.socket.close(1000, 'Normal closure');
      }
      this.socket = null;
    }

    this.subscribers.clear();
    this.statusSubscribers = [];
    this.reconnectAttempts = 0;
    this.activeSubscriptions = 0;
  }
}

export const webSocketService = new WebSocketService();
