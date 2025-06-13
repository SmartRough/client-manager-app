package com.smartrough.app.dao;

import java.sql.*;
import java.nio.file.*;

public class Database {

	private static final String DB_NAME = "client_manager.db";

	public static Connection connect() {
		Path dbPath = Paths.get(System.getProperty("user.dir")).resolve(DB_NAME);
		String url = "jdbc:sqlite:" + dbPath.toString();

		try {
			return DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.err.println("Error connecting to DB: " + e.getMessage());
			return null;
		}
	}

	public static void initializeSchema() {
		String sqlCompany = """
					CREATE TABLE IF NOT EXISTS Company (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						name TEXT NOT NULL,
						representative TEXT,
						phone TEXT,
						email TEXT,
						address_id INTEGER,
						is_own_company INTEGER DEFAULT 0,
						type TEXT DEFAULT 'BUSINESS',
						FOREIGN KEY(address_id) REFERENCES Address(id)
					);
				""";

		String sqlAddress = "CREATE TABLE IF NOT EXISTS Address (" + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "street TEXT NOT NULL," + "city TEXT NOT NULL," + "state TEXT NOT NULL," + "zip_code TEXT NOT NULL"
				+ ");";

		try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
			stmt.execute(sqlAddress);
			stmt.execute(sqlCompany);
		} catch (SQLException e) {
			System.err.println("Error creating schema: " + e.getMessage());
		}
	}
}
