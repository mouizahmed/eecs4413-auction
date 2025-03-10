package com.a2.bidding;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
/**
 * Main application class for the Auction System.
 * This class configures the base path for the REST API.
 */
@ApplicationPath("/api")
public class App extends ResourceConfig {
    public App() {
    	packages("com.a2.bidding");
    }
}
