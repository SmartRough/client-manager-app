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

		String sqlInvoice = """
					CREATE TABLE IF NOT EXISTS Invoice (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						invoice_number TEXT NOT NULL,
						date DATE NOT NULL,
						company_id INTEGER NOT NULL,
						customer_id INTEGER NOT NULL,
						subtotal REAL NOT NULL,
						tax_rate REAL,
						additional_costs REAL,
						total REAL NOT NULL,
						notes TEXT,
						FOREIGN KEY(company_id) REFERENCES Company(id),
						FOREIGN KEY(customer_id) REFERENCES Company(id)
					);
				""";

		String sqlInvoiceItem = """
					CREATE TABLE IF NOT EXISTS Invoice_Item (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						invoice_id INTEGER NOT NULL,
						description TEXT NOT NULL,
						amount REAL NOT NULL,
						FOREIGN KEY(invoice_id) REFERENCES Invoice(id) ON DELETE CASCADE
					);
				""";

		try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
			stmt.execute(sqlAddress);
			stmt.execute(sqlCompany);
			stmt.execute(sqlInvoice);
			stmt.execute(sqlInvoiceItem);
		} catch (SQLException e) {
			System.err.println("Error creating schema: " + e.getMessage());
		}
	}
}
