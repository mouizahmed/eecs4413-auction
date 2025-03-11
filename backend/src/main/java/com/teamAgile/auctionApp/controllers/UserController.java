package com.teamAgile.auctionApp.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import com.teamAgile.auctionApp.DatabaseConnection;
import com.teamAgile.auctionApp.models.User;
import com.teamAgile.auctionApp.repositories.UserDAO;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;


@Path("/user")
public class UserController {
	private UserDAO userDAO = new UserDAO();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String, User> main() {
		Connection conn = DatabaseConnection.connect();
		return userDAO.readAll();
	}
	
	@Path("/signin")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User signIn(User user) {
		return userDAO.signIn(user);
	}
	
	@Path("/signup")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User createUser(User user) {
		return userDAO.create(user);
	}
}
 