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

		String sqlEstimate = """
					CREATE TABLE IF NOT EXISTS Estimate (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						date DATE NOT NULL,
						company_id INTEGER NOT NULL,
						customer_id INTEGER NOT NULL,
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

		String sqlContractTemplate = """
					CREATE TABLE IF NOT EXISTS Contract_Template (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						name TEXT NOT NULL,
						legal_notice TEXT,
						contractor_id INTEGER NOT NULL,
						FOREIGN KEY(contractor_id) REFERENCES Company(id)
					);
				""";

		String sqlStandardClause = """
					CREATE TABLE IF NOT EXISTS Standard_Clause (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						template_id INTEGER NOT NULL,
						clause_order INTEGER,
						title TEXT NOT NULL,
						content TEXT NOT NULL,
						active INTEGER NOT NULL DEFAULT 1,
						FOREIGN KEY(template_id) REFERENCES Contract_Template(id)
					);
				""";

		String sqlContract = """
					CREATE TABLE IF NOT EXISTS Contract (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						contract_number TEXT,
						contract_date DATE,
						template_id INTEGER,
						client_info_id INTEGER,
						property_info_id INTEGER,
						financial_info_id INTEGER,
						status TEXT,
						FOREIGN KEY(template_id) REFERENCES Contract_Template(id),
						FOREIGN KEY(client_info_id) REFERENCES Contract_Client_Info(id),
						FOREIGN KEY(property_info_id) REFERENCES Project_Property_Info(id),
						FOREIGN KEY(financial_info_id) REFERENCES Contract_Financial_Info(id)
					);
				""";

		String sqlContractClientInfo = """
					CREATE TABLE IF NOT EXISTS Contract_Client_Info (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						company_name TEXT,
						owner_name1 TEXT,
						owner_name2 TEXT,
						email TEXT,
						address TEXT,
						home_phone TEXT,
						other_phone TEXT
					);
				""";

		String sqlContractItem = """
					CREATE TABLE IF NOT EXISTS Contract_Item (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						contract_id INTEGER NOT NULL,
						item_order INTEGER,
						description TEXT,
						FOREIGN KEY(contract_id) REFERENCES Contract(id) ON DELETE CASCADE
					);
				""";

		String sqlProjectPropertyInfo = """
					CREATE TABLE IF NOT EXISTS Project_Property_Info (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						contract_id INTEGER,
						is_home INTEGER,
						is_condo INTEGER,
						is_mfh INTEGER,
						is_commercial INTEGER,
						is_hoa INTEGER,
						FOREIGN KEY(contract_id) REFERENCES Contract(id) ON DELETE CASCADE
					);
				""";

		String sqlContractFinancialInfo = """
					CREATE TABLE IF NOT EXISTS Contract_Financial_Info (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						contract_id INTEGER,
						total_price REAL,
						deposit REAL,
						balance_due_upon_completion REAL,
						amount_financed REAL,
						FOREIGN KEY(contract_id) REFERENCES Contract(id) ON DELETE CASCADE
					);
				""";

		String sqlAttachment = """
					CREATE TABLE IF NOT EXISTS Attachment (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						contract_id INTEGER,
						file_name TEXT,
						file_type TEXT,
						FOREIGN KEY(contract_id) REFERENCES Contract(id) ON DELETE CASCADE
					);
				""";

		String sqlSignature = """
					CREATE TABLE IF NOT EXISTS Signature (
						id INTEGER PRIMARY KEY AUTOINCREMENT,
						contract_id INTEGER,
						name TEXT,
						role TEXT,
						date_signed DATE,
						image_name TEXT,
						FOREIGN KEY(contract_id) REFERENCES Contract(id) ON DELETE CASCADE
					);
				""";

		String sqlClauseStatus = """
					CREATE TABLE IF NOT EXISTS Contract_Clause_Status (
						contract_id INTEGER NOT NULL,
						clause_id INTEGER NOT NULL,
						initialed INTEGER NOT NULL DEFAULT 0,
						PRIMARY KEY (contract_id, clause_id),
						FOREIGN KEY(contract_id) REFERENCES Contract(id) ON DELETE CASCADE,
						FOREIGN KEY(clause_id) REFERENCES Standard_Clause(id) ON DELETE CASCADE
					);
				""";

		try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
			stmt.execute(sqlAddress);
			stmt.execute(sqlCompany);
			stmt.execute(sqlInvoice);
			stmt.execute(sqlInvoiceItem);
			stmt.execute(sqlEstimate);
			stmt.execute(sqlEstimateItem);
			stmt.execute(sqlContractTemplate);
			stmt.execute(sqlStandardClause);
			stmt.execute(sqlContractClientInfo);
			stmt.execute(sqlContract);
			stmt.execute(sqlContractItem);
			stmt.execute(sqlProjectPropertyInfo);
			stmt.execute(sqlContractFinancialInfo);
			stmt.execute(sqlAttachment);
			stmt.execute(sqlSignature);
			stmt.execute(sqlClauseStatus);

		} catch (SQLException e) {
			System.err.println("Error creating schema: " + e.getMessage());
		}
	}
}
