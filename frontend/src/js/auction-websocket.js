/**
 * WebSocket client for handling auction updates
 */
class AuctionWebSocket {
    /**
     * Create a new WebSocket client for auction updates
     * @param {string} url - The WebSocket server URL
     * @param {Object} callbacks - Callback functions for WebSocket events
     */
    constructor(url, callbacks = {}) {
        this.url = url;
        this.socket = null;
        this.isConnected = false;
        
        // Default callbacks
        this.callbacks = {
            onConnect: () => console.log('Connected to auction updates WebSocket'),
            onDisconnect: () => console.log('Disconnected from auction updates WebSocket'),
            onMessage: (data) => console.log('Received auction update:', data),
            onError: (error) => console.error('Auction WebSocket error:', error),
            onAuctionUpdate: (update) => console.log('Auction update:', update),
            ...callbacks
        };
    }
    
    /**
     * Connect to the WebSocket server
     * @returns {Promise} A promise that resolves when connected or rejects on error
     */
    connect() {
        return new Promise((resolve, reject) => {
            if (this.isConnected) {
                resolve();
                return;
            }
            
            try {
                this.socket = new WebSocket(this.url);
                
                this.socket.addEventListener('open', (event) => {
                    this.isConnected = true;
                    this.callbacks.onConnect(event);
                    resolve();
                });
                
                this.socket.addEventListener('message', (event) => {
                    try {
                        const data = JSON.parse(event.data);
                        
                        // Handle different message types
                        if (data.type === 'AUCTION_UPDATE') {
                            this.callbacks.onAuctionUpdate(data);
                        }
                        
                        this.callbacks.onMessage(data);
                    } catch (error) {
                        console.error('Error parsing WebSocket message:', error);
                        this.callbacks.onError(error);
                    }
                });
                
                this.socket.addEventListener('close', (event) => {
                    this.isConnected = false;
                    this.callbacks.onDisconnect(event);
                });
                
                this.socket.addEventListener('error', (event) => {
                    this.callbacks.onError(event);
                    reject(event);
                });
            } catch (error) {
                this.callbacks.onError(error);
                reject(error);
            }
        });
    }
    
    /**
     * Subscribe to updates for a specific auction item
     * @param {string} itemId - The ID of the auction item to subscribe to
     */
    subscribeToItem(itemId) {
        if (!this.isConnected || !this.socket) {
            throw new Error('WebSocket is not connected');
        }
        
        const subscribeMessage = {
            type: 'SUBSCRIBE',
            itemId: itemId
        };
        
        this.socket.send(JSON.stringify(subscribeMessage));
    }
    
    /**
     * Disconnect from the WebSocket server
     */
    disconnect() {
        if (this.socket && this.isConnected) {
            this.socket.close();
            this.socket = null;
            this.isConnected = false;
        }
    }
    
    /**
     * Check if the WebSocket is connected
     * @returns {boolean} True if connected, false otherwise
     */
    isConnected() {
        return this.isConnected;
    }
}

// Export the class for use in other modules
if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
    module.exports = AuctionWebSocket;
} else {
    window.AuctionWebSocket = AuctionWebSocket;
} 