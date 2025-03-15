/**
 * Main application script
 */
document.addEventListener('DOMContentLoaded', function() {
    // Elements
    const connectBtn = document.getElementById('connect');
    const disconnectBtn = document.getElementById('disconnect');
    const statusDiv = document.getElementById('status');
    const notificationsDiv = document.getElementById('notifications');
    
    // Create WebSocket client
    const wsClient = new JoinNotificationWebSocket('ws://localhost:8080/ws/join-notifications', {
        onConnect: (event) => {
            updateConnectionStatus(true);
            addNotification('Connected to WebSocket server');
        },
        onDisconnect: (event) => {
            updateConnectionStatus(false);
            addNotification('Disconnected from WebSocket server');
        },
        onUserJoined: (userId, message) => {
            addNotification(`${message} (ID: ${userId})`);
        },
        onError: (error) => {
            updateConnectionStatus(false, 'Error');
            addNotification('WebSocket connection error');
            console.error('WebSocket error:', error);
        }
    });
    
    // Connect button
    connectBtn.addEventListener('click', async () => {
        try {
            await wsClient.connect();
        } catch (error) {
            console.error('Failed to connect:', error);
        }
    });
    
    // Disconnect button
    disconnectBtn.addEventListener('click', () => {
        wsClient.disconnect();
    });
    
    // Helper function to update connection status UI
    function updateConnectionStatus(isConnected, customText) {
        if (isConnected) {
            statusDiv.textContent = customText || 'Connected';
            statusDiv.classList.remove('disconnected');
            statusDiv.classList.add('connected');
            
            connectBtn.disabled = true;
            disconnectBtn.disabled = false;
        } else {
            statusDiv.textContent = customText || 'Disconnected';
            statusDiv.classList.remove('connected');
            statusDiv.classList.add('disconnected');
            
            connectBtn.disabled = false;
            disconnectBtn.disabled = true;
        }
    }
    
    // Helper function to add notifications
    function addNotification(message) {
        const notificationDiv = document.createElement('div');
        notificationDiv.className = 'notification';
        notificationDiv.textContent = message;
        
        notificationsDiv.appendChild(notificationDiv);
        notificationsDiv.scrollTop = notificationsDiv.scrollHeight;
    }
}); 