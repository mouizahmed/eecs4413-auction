import { ConnectionStatus, WebSocketMessage } from '@/types';

class WebSocketService {
  private socket: WebSocket | null = null;
  private subscribers: Map<string, ((data: WebSocketMessage) => void)[]> = new Map();
  private statusSubscribers: ((status: ConnectionStatus) => void)[] = [];

  private connect() {
    if (this.socket) return;

    this.notifyStatusChange('connecting');
    this.socket = new WebSocket('ws://localhost:8080/ws/auction-updates');

    this.socket.onopen = () => {
      this.notifyStatusChange('connected');
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

    this.socket.onerror = () => this.notifyStatusChange('error');

    this.socket.onclose = () => {
      this.socket = null;
      this.notifyStatusChange('disconnected');
      setTimeout(() => this.connect(), 5000);
    };
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
    };
  }

  subscribeToStatus(callback: (status: ConnectionStatus) => void) {
    this.statusSubscribers.push(callback);
    return () => {
      this.statusSubscribers = this.statusSubscribers.filter((cb) => cb !== callback);
    };
  }

  disconnect() {
    this.socket?.close();
    this.socket = null;
    this.subscribers.clear();
    this.statusSubscribers = [];
  }
}

export const webSocketService = new WebSocketService();
