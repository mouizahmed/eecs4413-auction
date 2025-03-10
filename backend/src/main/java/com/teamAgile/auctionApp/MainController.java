package com.teamAgile.auctionApp;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;


@Path("/")
public class MainController {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String main() {
		return "Hello world!!!!!!";
	}
	

}
