package com.a2.bidding;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users") // Base path for user-related endpoints
public class UserController {
    
    private UserService userService = new UserService();

    @POST
    @Path("/signup") // Endpoint for sign-up
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED) // Handle form data
    @Produces(MediaType.APPLICATION_JSON) // Return JSON response
    public Response signUp(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("firstName") String firstName,
            @FormParam("lastName") String lastName,
            @FormParam("shippingAddress") String shippingAddress) {

        // Create a new User object
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setShippingAddress(shippingAddress);

        // Call the service to sign up the user
        User createdUser = userService.signUp(user);

        // Return a response
        return Response.status(Response.Status.CREATED).entity(createdUser).build();
    }
    
    @POST
    @Path("/signin")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED) // Handle form data
    @Produces(MediaType.APPLICATION_JSON) // Return JSON response
    public Response signIn(
            @FormParam("username") String username,
            @FormParam("password") String password) {

        // Call the service to sign in the user
        User loggedInUser = userService.signIn(username, password);

        // Return a response
        if (loggedInUser != null) {
            return Response.ok(loggedInUser).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid username or password").build();
        }



    }
    
}    