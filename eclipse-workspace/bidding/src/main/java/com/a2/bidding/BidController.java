package com.a2.bidding;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/bids") // Base path for bid-related endpoints
public class BidController {
    @Inject
    private BidService bidService;

    // Endpoint to place a bid in a forward auction
    @POST
    @Path("/forward")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED) // Handle form data
    @Produces(MediaType.APPLICATION_JSON) // Return JSON response
    public Response placeForwardBid(
            @FormParam("itemId") Long itemId,
            @FormParam("bidderId") Long bidderId,
            @FormParam("bidAmount") double bidAmount) {

        // Create a new Bid object
        Bid bid = new Bid();
        bid.setItemId(itemId);
        bid.setBidderId(bidderId);
        bid.setBidAmount(bidAmount);

        // Call the service to place the bid
        Bid placedBid = bidService.placeForwardBid(bid);

        // Return a response
        return Response.status(Response.Status.CREATED).entity(placedBid).build();
    }

    // Endpoint to buy now in a Dutch auction
    @POST
    @Path("/dutch/buynow")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED) // Handle form data
    @Produces(MediaType.APPLICATION_JSON) // Return JSON response
    public Response buyNowDutchAuction(
            @FormParam("itemId") Long itemId,
            @FormParam("bidderId") Long bidderId) {

        // Call the service to buy now
        Bid winningBid = bidService.buyNowDutchAuction(itemId, bidderId);

        // Return a response
        return Response.ok(winningBid).build();
    }
}
