package com.a2.bidding;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;

@Path("/items") // Base path for item-related endpoints
public class Itemcontroller {

   
    private ItemService itemService;

    // Endpoint to search items by keyword
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchItems(@QueryParam("keyword") String keyword) {
        List<Item> items = itemService.searchItems(keyword);
        return Response.ok(items).build();
    }

    // Endpoint to sell an item
    @POST
    @Path("/sell")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sellItem(
            @FormParam("name") String name,
            @FormParam("description") String description,
            @FormParam("auctionType") String auctionType,
            @FormParam("initialPrice") double initialPrice,
            @FormParam("currentPrice") double currentPrice,
            @FormParam("endTime") String endTime,
            @FormParam("sellerId") Long sellerId) {

        // Create a new Item object
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAuctionType(auctionType);
        item.setInitialPrice(initialPrice);
        item.setCurrentPrice(currentPrice);
        item.setEndTime(endTime); // You may need to parse this into a LocalDateTime
        item.setSellerId(sellerId);

        // Call the service to add the item
        Item createdItem = itemService.addItem(item);

        // Return a response
        return Response.status(Response.Status.CREATED).entity(createdItem).build();
    }

    // Endpoint to get all items (optional)
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllItems() {
        List<Item> items = itemService.getAllItems();
        return Response.ok(items).build();
    }
}