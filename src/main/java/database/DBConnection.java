package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL = "jdbc:sqlite:trip.db";

    public static Connection connect() throws Exception {
        return DriverManager.getConnection(URL);
    }
}