package database;

import java.sql.Connection;
import java.sql.Statement;

public class DBSetup {

    public static void init() {

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT UNIQUE,
                    password TEXT
                )
            """);

            stmt.execute("""
                INSERT OR IGNORE INTO users(email,password)
                VALUES('admin','123')
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS trips(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT,
                    days INTEGER,
                    budget REAL
                )
              """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS travelers(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    trip_id INTEGER,
                    name TEXT
                )
              """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS expenses(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    trip_id INTEGER,
                    payer TEXT,
                    amount REAL,
                    description TEXT
                )
              """);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}