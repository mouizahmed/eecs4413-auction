/**
 * Auction Detail Page JavaScript
 * This file handles the auction detail page functionality, including:
 * - Fetching auction item details
 * - Connecting to WebSocket for real-time updates
 * - Placing bids
 */

// Base API URL - replace with your actual API URL
const API_BASE_URL = 'http://localhost:8080';
// WebSocket URL for auction updates
const WS_AUCTION_URL = 'ws://localhost:8080/ws/auction-updates';

// DOM elements
let auctionItemElement;
let currentPriceElement;
let highestBidderElement;
let bidFormElement;
let bidAmountInput;
let bidButton;
let statusElement;
let auctionUpdatesElement;

// Current auction data
let currentAuctionItem = null;

// WebSocket connection
let auctionSocket = null;

/**
 * Initialize the page
 */
document.addEventListener('DOMContentLoaded', () => {
    // Get DOM elements
    auctionItemElement = document.getElementById('auction-item');
    currentPriceElement = document.getElementById('current-price');
    highestBidderElement = document.getElementById('highest-bidder');
    bidFormElement = document.getElementById('bid-form');
    bidAmountInput = document.getElementById('bid-amount');
    bidButton = document.getElementById('bid-button');
    statusElement = document.getElementById('status');
    auctionUpdatesElement = document.getElementById('auction-updates');
    
    // Get the auction item ID from the URL
    const urlParams = new URLSearchParams(window.location.search);
    const itemId = urlParams.get('id');
    
    if (!itemId) {
        showError('No auction item ID specified');
        return;
    }
    
    // Initialize the page with the auction item
    initializeAuctionItem(itemId);
    
    // Set up the bid form
    if (bidFormElement) {
        bidFormElement.addEventListener('submit', (event) => {
            event.preventDefault();
            placeBid(itemId);
        });
    }
});

/**
 * Initialize the auction item
 * @param {string} itemId - The ID of the auction item
 */
async function initializeAuctionItem(itemId) {
    try {
        // Fetch the auction item details
        const response = await fetch(`${API_BASE_URL}/auction/item?id=${itemId}`);
        if (!response.ok) {
            throw new Error(`Failed to fetch auction item: ${response.statusText}`);
        }
        
        const auctionItem = await response.json();
        currentAuctionItem = auctionItem;
        
        // Update the UI with the auction item details
        updateAuctionUI(auctionItem);
        
        // Connect to the WebSocket for real-time updates
        connectToWebSocket(itemId);
        
    } catch (error) {
        showError(`Error loading auction item: ${error.message}`);
    }
}

/**
 * Connect to the WebSocket for real-time auction updates
 * @param {string} itemId - The ID of the auction item
 */
function connectToWebSocket(itemId) {
    // Create a new WebSocket connection
    auctionSocket = new AuctionWebSocket(WS_AUCTION_URL, {
        onConnect: () => {
            console.log('Connected to auction updates');
            // Subscribe to updates for this specific auction item
            auctionSocket.subscribeToItem(itemId);
            addUpdateMessage('Connected to auction updates');
        },
        onDisconnect: () => {
            console.log('Disconnected from auction updates');
            addUpdateMessage('Disconnected from auction updates');
        },
        onAuctionUpdate: (update) => {
            console.log('Received auction update:', update);
            
            // Only update if this is for our current item
            if (update.itemId === itemId) {
                // Update the UI with the new auction data
                updateAuctionUI({
                    itemName: update.itemName,
                    currentPrice: update.currentPrice,
                    highestBidder: update.highestBidder,
                    auctionStatus: update.auctionStatus
                });
                
                addUpdateMessage(`Auction updated: Current price is now $${update.currentPrice}`);
            }
        },
        onError: (error) => {
            console.error('WebSocket error:', error);
            addUpdateMessage(`WebSocket error: ${error.message || 'Unknown error'}`);
        }
    });
    
    // Connect to the WebSocket
    auctionSocket.connect()
        .catch(error => {
            showError(`Failed to connect to WebSocket: ${error.message}`);
        });
}

/**
 * Update the UI with auction item details
 * @param {Object} auctionItem - The auction item data
 */
function updateAuctionUI(auctionItem) {
    if (auctionItemElement) {
        auctionItemElement.textContent = auctionItem.itemName;
    }
    
    if (currentPriceElement) {
        currentPriceElement.textContent = `$${auctionItem.currentPrice.toFixed(2)}`;
    }
    
    if (highestBidderElement) {
        highestBidderElement.textContent = auctionItem.highestBidder || 'No bids yet';
    }
    
    if (statusElement) {
        statusElement.textContent = auctionItem.auctionStatus;
        
        // Disable the bid form if the auction is not available
        if (auctionItem.auctionStatus !== 'AVAILABLE') {
            if (bidButton) bidButton.disabled = true;
            if (bidAmountInput) bidAmountInput.disabled = true;
            
            statusElement.classList.add('status-' + auctionItem.auctionStatus.toLowerCase());
        } else {
            if (bidButton) bidButton.disabled = false;
            if (bidAmountInput) bidAmountInput.disabled = false;
            
            // Set minimum bid amount for forward auctions
            if (auctionItem.auctionType === 'FORWARD' && bidAmountInput) {
                bidAmountInput.min = auctionItem.currentPrice + 0.01;
                bidAmountInput.placeholder = `Min bid: $${(auctionItem.currentPrice + 0.01).toFixed(2)}`;
            }
        }
    }
}

/**
 * Place a bid on the auction item
 * @param {string} itemId - The ID of the auction item
 */
async function placeBid(itemId) {
    if (!bidAmountInput || !bidAmountInput.value) {
        showError('Please enter a bid amount');
        return;
    }
    
    const bidAmount = parseFloat(bidAmountInput.value);
    
    if (isNaN(bidAmount) || bidAmount <= 0) {
        showError('Please enter a valid bid amount');
        return;
    }
    
    // For forward auctions, ensure the bid is higher than the current price
    if (currentAuctionItem.auctionType === 'FORWARD' && bidAmount <= currentAuctionItem.currentPrice) {
        showError(`Bid must be higher than the current price ($${currentAuctionItem.currentPrice.toFixed(2)})`);
        return;
    }
    
    // For Dutch auctions, ensure the bid matches the current price
    if (currentAuctionItem.auctionType === 'DUTCH' && bidAmount !== currentAuctionItem.currentPrice) {
        showError(`For Dutch auctions, bid must match the current price ($${currentAuctionItem.currentPrice.toFixed(2)})`);
        return;
    }
    
    try {
        // Disable the bid button while processing
        if (bidButton) bidButton.disabled = true;
        
        // Determine the endpoint based on auction type
        const endpoint = currentAuctionItem.auctionType === 'FORWARD' 
            ? `${API_BASE_URL}/auction/forward/${itemId}/bid`
            : `${API_BASE_URL}/auction/dutch/${itemId}/bid`;
        
        // Send the bid to the server
        const response = await fetch(endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ bidPrice: bidAmount }),
            credentials: 'include' // Include cookies for authentication
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData || response.statusText);
        }
        
        const bidResult = await response.json();
        console.log('Bid placed successfully:', bidResult);
        
        // Clear the bid input
        if (bidAmountInput) bidAmountInput.value = '';
        
        addUpdateMessage(`Bid of $${bidAmount.toFixed(2)} placed successfully`);
        
        // Note: We don't need to update the UI here because we'll receive a WebSocket update
        
    } catch (error) {
        showError(`Failed to place bid: ${error.message}`);
    } finally {
        // Re-enable the bid button
        if (bidButton) bidButton.disabled = false;
    }
}

/**
 * Add an update message to the updates list
 * @param {string} message - The message to add
 */
function addUpdateMessage(message) {
    if (!auctionUpdatesElement) return;
    
    const timestamp = new Date().toLocaleTimeString();
    const messageElement = document.createElement('div');
    messageElement.className = 'update-message';
    messageElement.innerHTML = `<span class="timestamp">${timestamp}</span> ${message}`;
    
    auctionUpdatesElement.appendChild(messageElement);
    auctionUpdatesElement.scrollTop = auctionUpdatesElement.scrollHeight;
}

/**
 * Show an error message
 * @param {string} message - The error message to show
 */
function showError(message) {
    console.error(message);
    addUpdateMessage(`Error: ${message}`);
    
    // You could also show a toast or alert here
}

/**
 * Clean up when leaving the page
 */
window.addEventListener('beforeunload', () => {
    // Disconnect from the WebSocket
    if (auctionSocket) {
        auctionSocket.disconnect();
    }
}); 