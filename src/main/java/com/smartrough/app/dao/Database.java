package com.smartrough.app.dao;

import java.sql.*;
import java.nio.file.*;

public class Database {

	private static final String DB_NAME = "client_manager.db";

	public static Connection connect() {
		Path dbPath = Paths.get(System.getProperty("user.dir")).resolve(DB_NAME);
		String url = "jdbc:sqlite:" + dbPath.toString();

		try {
			Connection conn = DriverManager.getConnection(url);

			try (Statement stmt = conn.createStatement()) {
				stmt.execute("PRAGMA foreign_keys = ON");
			}

			return conn;
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
						license TEXT,
						FOREIGN KEY(address_id) REFERENCES Address(id)
					);
				""";

		String sqlAddress = """
					CREATE TABLE IF NOT EXISTS Address (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						street TEXT NOT NULL,
						city TEXT NOT NULL,
						state TEXT NOT NULL,
						zip_code TEXT NOT NULL
					);
				""";

		String sqlInvoice = """
					CREATE TABLE IF NOT EXISTS Invoice (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						invoice_number TEXT NOT NULL UNIQUE,
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

		String sqlEstimate = """
					CREATE TABLE IF NOT EXISTS Estimate (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						date DATE NOT NULL,
						company_id INTEGER NOT NULL,
						customer_id INTEGER NOT NULL,
						approved_by TEXT,
						job_description TEXT,
						total REAL NOT NULL,
						image_names TEXT,
						FOREIGN KEY(company_id) REFERENCES Company(id),
						FOREIGN KEY(customer_id) REFERENCES Company(id)
					);
				""";

		String sqlEstimateItem = """
					CREATE TABLE IF NOT EXISTS Estimate_Item (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						estimate_id INTEGER NOT NULL,
						description TEXT NOT NULL,
						FOREIGN KEY(estimate_id) REFERENCES Estimate(id) ON DELETE CASCADE
					);
				""";

		String sqlContract = """
					CREATE TABLE IF NOT EXISTS Contract (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						po_number TEXT UNIQUE,
						measure_date DATE,
						startDate DATE,
						endDate DATE,
						owner1 TEXT,
						owner2 TEXT,
						address TEXT,
						city TEXT,
						state TEXT,
						zip TEXT,
						email TEXT,
						home_phone TEXT,
						other_phone TEXT,
						is_house INTEGER DEFAULT 0,
						is_condo INTEGER DEFAULT 0,
						is_mfh INTEGER DEFAULT 0,
						is_commercial INTEGER DEFAULT 0,
						has_hoa INTEGER DEFAULT 0,
						total_price REAL,
						deposit REAL,
						balance_due REAL,
						amount_financed REAL,
						card_type TEXT,
						card_number TEXT,
						card_zip TEXT,
						card_cvc TEXT,
						card_exp TEXT
					);
				""";

		String sqlContractItem = """
					CREATE TABLE IF NOT EXISTS Contract_Item (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						contract_id INTEGER NOT NULL,
						description TEXT,
						"order" INTEGER NOT NULL,
						FOREIGN KEY(contract_id) REFERENCES Contract(id) ON DELETE CASCADE
					);
				""";

		String sqlContractClauseItem = """
					CREATE TABLE IF NOT EXISTS Contract_Clause_Item (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						contract_id INTEGER NOT NULL,
						clause_order INTEGER,
						description TEXT,
						FOREIGN KEY(contract_id) REFERENCES Contract(id) ON DELETE CASCADE
					);
				""";

		String sqlContractAttachment = """
					CREATE TABLE IF NOT EXISTS Contract_Attachment (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						contract_id INTEGER NOT NULL,
						name TEXT NOT NULL,
						extension TEXT NOT NULL,
						FOREIGN KEY(contract_id) REFERENCES Contract(id) ON DELETE CASCADE
					);
				""";

		try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
			stmt.execute(sqlAddress);
			stmt.execute(sqlCompany);
			stmt.execute(sqlInvoice);
			stmt.execute(sqlInvoiceItem);
			stmt.execute(sqlEstimate);
			stmt.execute(sqlEstimateItem);
			stmt.execute(sqlContract);
			stmt.execute(sqlContractItem);
			stmt.execute(sqlContractClauseItem);
			stmt.execute(sqlContractAttachment);

			// Insertar compañía por defecto si no existe
			try (PreparedStatement checkStmt = conn
					.prepareStatement("SELECT COUNT(*) FROM Company WHERE is_own_company = 1");
					ResultSet rs = checkStmt.executeQuery()) {
				if (rs.next() && rs.getInt(1) == 0) {
					// Insertar dirección
					String insertAddress = """
								INSERT INTO Address (street, city, state, zip_code)
								VALUES (?, ?, ?, ?)
							""";
					long addressId;
					try (PreparedStatement addrStmt = conn.prepareStatement(insertAddress,
							Statement.RETURN_GENERATED_KEYS)) {
						addrStmt.setString(1, "7028 W. Waters Ave. #101");
						addrStmt.setString(2, "Tampa");
						addrStmt.setString(3, "FL");
						addrStmt.setString(4, "33634");
						addrStmt.executeUpdate();

						try (ResultSet addrKeys = addrStmt.getGeneratedKeys()) {
							if (addrKeys.next()) {
								addressId = addrKeys.getLong(1);
							} else {
								throw new SQLException("Failed to retrieve address ID.");
							}
						}
					}

					// Insertar compañía
					String insertCompany = """
								INSERT INTO Company (name, representative, phone, email, address_id, is_own_company, type, license)
								VALUES (?, ?, ?, ?, ?, ?, ?, ?)
							""";
					try (PreparedStatement compStmt = conn.prepareStatement(insertCompany)) {
						compStmt.setString(1, "G Construction Service Inc.");
						compStmt.setString(2, "Greg Staples");
						compStmt.setString(3, "813-613-2040");
						compStmt.setString(4, "gcs@usa.com");
						compStmt.setLong(5, addressId);
						compStmt.setInt(6, 1); // is_own_company = true
						compStmt.setString(7, "BUSINESS");
						compStmt.setString(8, "CGC 1532364");
						compStmt.executeUpdate();
					}
				}
			}

		} catch (SQLException e) {
			System.err.println("Error creating schema: " + e.getMessage());
		}
	}
}
