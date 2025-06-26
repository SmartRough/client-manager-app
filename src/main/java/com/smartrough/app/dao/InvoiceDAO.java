package com.smartrough.app.dao;

import com.smartrough.app.model.Invoice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

	public static long save(Invoice invoice) {
		if (existsInvoiceNumber(invoice.getInvoiceNumber())) {
			throw new IllegalArgumentException("Invoice number already exists.");
		}

		String[] columns = { "invoice_number", "date", "company_id", "customer_id", "subtotal", "tax_rate",
				"additional_costs", "total", "notes" };

		Object[] values = { invoice.getInvoiceNumber(), invoice.getDate(), invoice.getCompanyId(),
				invoice.getCustomerId(), invoice.getSubtotal(), invoice.getTaxRate(), invoice.getAdditionalCosts(),
				invoice.getTotal(), invoice.getNotes() };

		int[] types = { Types.VARCHAR, Types.DATE, Types.BIGINT, Types.BIGINT, Types.DECIMAL, Types.DECIMAL,
				Types.DECIMAL, Types.DECIMAL, Types.VARCHAR };

		return CRUDHelper.create("Invoice", columns, values, types);
	}

	public static List<Invoice> findAll() {
		List<Invoice> list = new ArrayList<>();
		String sql = "SELECT * FROM Invoice ORDER BY id DESC";

		try (Connection conn = Database.connect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				list.add(mapResultSet(rs));
			}

		} catch (SQLException e) {
			System.err.println("Error fetching invoices: " + e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	public static Invoice findById(long id) {
		String sql = "SELECT * FROM Invoice WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				return mapResultSet(rs);
		} catch (SQLException e) {
			System.err.println("Error fetching invoice: " + e.getMessage());
		}
		return null;
	}

	public static boolean update(Invoice invoice) {
		String sql = """
					UPDATE Invoice SET invoice_number=?, date=?, company_id=?, customer_id=?, subtotal=?, tax_rate=?,
						additional_costs=?, total=?, notes=? WHERE id=?
				""";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, invoice.getInvoiceNumber());
			stmt.setString(2, invoice.getDate() != null ? invoice.getDate().toString() : null);
			stmt.setLong(3, invoice.getCompanyId());
			stmt.setLong(4, invoice.getCustomerId());
			stmt.setBigDecimal(5, invoice.getSubtotal());
			stmt.setBigDecimal(6, invoice.getTaxRate());
			stmt.setBigDecimal(7, invoice.getAdditionalCosts());
			stmt.setBigDecimal(8, invoice.getTotal());
			stmt.setString(9, invoice.getNotes());
			stmt.setLong(10, invoice.getId());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error updating invoice: " + e.getMessage());
			return false;
		}
	}

	public static boolean existsInvoiceNumber(String invoiceNumber) {
		String sql = "SELECT COUNT(*) FROM Invoice WHERE invoice_number = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, invoiceNumber);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			System.err.println("Error checking invoice number: " + e.getMessage());
		}
		return false;
	}

	public static boolean delete(long id) {
		String sql = "DELETE FROM Invoice WHERE id=?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting invoice: " + e.getMessage());
			return false;
		}
	}

	public static long findLastInvoiceNumber() {
		String sql = "SELECT MAX(CAST(invoice_number AS INTEGER)) AS max_number FROM Invoice";
		try (Connection conn = Database.connect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				return rs.getLong("max_number");
			}
		} catch (Exception e) {
			System.err.println("Error retrieving max invoice number: " + e.getMessage());
		}
		return 0;
	}

	private static Invoice mapResultSet(ResultSet rs) throws SQLException {
		Invoice i = new Invoice();
		i.setId(rs.getLong("id"));
		i.setInvoiceNumber(rs.getString("invoice_number"));

		try {
			String dateStr = rs.getString("date");
			if (dateStr != null && !dateStr.isBlank()) {
				i.setDate(java.time.LocalDate.parse(dateStr));
			}
		} catch (Exception ex) {
			System.err.println(">> ERROR parsing invoice date: " + ex.getMessage());
			ex.printStackTrace();
			i.setDate(null);
		}

		i.setCompanyId(rs.getLong("company_id"));
		i.setCustomerId(rs.getLong("customer_id"));
		i.setSubtotal(rs.getBigDecimal("subtotal"));

		try {
			i.setTaxRate(rs.getBigDecimal("tax_rate"));
		} catch (SQLException e) {
			System.err.println(">> tax_rate could not be retrieved: " + e.getMessage());
			i.setTaxRate(null);
		}

		try {
			i.setAdditionalCosts(rs.getBigDecimal("additional_costs"));
		} catch (SQLException e) {
			System.err.println(">> additional_costs could not be retrieved: " + e.getMessage());
			i.setAdditionalCosts(null);
		}

		i.setTotal(rs.getBigDecimal("total"));
		i.setNotes(rs.getString("notes"));

		return i;
	}
}
