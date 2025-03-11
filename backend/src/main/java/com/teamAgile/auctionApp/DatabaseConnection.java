package com.teamAgile.auctionApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://ep-young-tooth-a551pdzg-pooler.us-east-2.aws.neon.tech/auctiondb?sslmode=require";
    private static final String USER = "auctiondb_owner";
    private static final String PASSWORD = "npg_lLr5Hfjgy0cx";

    public static Connection connect() {
        Connection conn = null;
        try {
            // Load the PostgreSQL JDBC Driver
            Class.forName("org.postgresql.Driver");
            // Establish the connection
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("CONNECTED");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver not found. Include it in your library path!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
        return conn;
    }
}

