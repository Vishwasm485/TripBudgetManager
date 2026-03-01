package database;

import java.sql.Connection;
import java.sql.Statement;

public class DBSetup {

    public static void init() {

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {

            // Enable foreign keys in SQLite
            stmt.execute("PRAGMA foreign_keys = ON");

            // ================= USERS TABLE =================
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                )
            """);

            // Default admin (only if not exists)
            stmt.execute("""
                INSERT OR IGNORE INTO users(email,password)
                VALUES('admin','123')
            """);

            // ================= TRIPS TABLE =================
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS trips(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    name TEXT,
                    days INTEGER,
                    budget REAL,
                    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
                )
            """);

            // ================= TRAVELERS TABLE =================
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS travelers(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    trip_id INTEGER NOT NULL,
                    name TEXT,
                    FOREIGN KEY(trip_id) REFERENCES trips(id) ON DELETE CASCADE
                )
            """);

            // ================= EXPENSES TABLE =================
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS expenses(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    trip_id INTEGER NOT NULL,
                    payer TEXT,
                    amount REAL,
                    description TEXT,
                    FOREIGN KEY(trip_id) REFERENCES trips(id) ON DELETE CASCADE
                )
            """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}