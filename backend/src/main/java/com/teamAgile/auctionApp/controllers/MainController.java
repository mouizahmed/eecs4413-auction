package com.teamAgile.auctionApp.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;


@Path("/")
public class MainController {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String main() {
		return "Hello world!!!!!!";
	}
	

}
