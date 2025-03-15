/**
 * WebSocket client for handling join notifications
 */
class JoinNotificationWebSocket {
    /**
     * Create a new WebSocket client
     * @param {string} url - The WebSocket server URL
     * @param {Object} callbacks - Callback functions for WebSocket events
     */
    constructor(url, callbacks = {}) {
        this.url = url;
        this.socket = null;
        this.isConnected = false;
        
        // Default callbacks
        this.callbacks = {
            onConnect: () => console.log('Connected to WebSocket server'),
            onDisconnect: () => console.log('Disconnected from WebSocket server'),
            onMessage: (data) => console.log('Received message:', data),
            onError: (error) => console.error('WebSocket error:', error),
            onUserJoined: (userId, message) => console.log(`User joined: ${userId} - ${message}`),
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
                        if (data.type === 'USER_JOINED') {
                            this.callbacks.onUserJoined(data.userId, data.message);
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
    module.exports = JoinNotificationWebSocket;
} else {
    window.JoinNotificationWebSocket = JoinNotificationWebSocket;
} 