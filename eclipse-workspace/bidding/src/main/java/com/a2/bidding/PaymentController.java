package com.a2.bidding;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/payments") // Base path for payment-related endpoints
public class PaymentController {
    
    private PaymentService paymentService;

    // Endpoint to process a payment
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED) // Handle form data
    @Produces(MediaType.APPLICATION_JSON) // Return JSON response
    public Response processPayment(
            @FormParam("itemId") Long itemId,
            @FormParam("userId") Long userId,
            @FormParam("amount") double amount,
            @FormParam("paymentMethod") String paymentMethod) {

        // Create a new Payment object
        Payment payment = new Payment();
        payment.setItemId(itemId);
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);

        // Call the service to process the payment
        Payment processedPayment = paymentService.processPayment(payment);

        // Return a response
        return Response.status(Response.Status.CREATED).entity(processedPayment).build();
    }
}