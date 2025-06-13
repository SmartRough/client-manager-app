package com.smartrough.app.dao;

import com.smartrough.app.model.InvoiceItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceItemDAO {

	public static long save(InvoiceItem item) {
		String[] columns = { "invoice_id", "description", "amount" };
		Object[] values = { item.getInvoiceId(), item.getDescription(), item.getAmount() };
		int[] types = { Types.BIGINT, Types.VARCHAR, Types.DECIMAL };
		return CRUDHelper.create("Invoice_Item", columns, values, types);
	}

	public static List<InvoiceItem> findByInvoiceId(long invoiceId) {
		List<InvoiceItem> items = new ArrayList<>();
		String sql = "SELECT * FROM Invoice_Item WHERE invoice_id = ?";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, invoiceId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				InvoiceItem item = new InvoiceItem();
				item.setId(rs.getLong("id"));
				item.setInvoiceId(rs.getLong("invoice_id"));
				item.setDescription(rs.getString("description"));
				item.setAmount(rs.getBigDecimal("amount"));
				items.add(item);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching invoice items: " + e.getMessage());
		}

		return items;
	}

	public static boolean update(InvoiceItem item) {
		String sql = "UPDATE Invoice_Item SET description = ?, amount = ? WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, item.getDescription());
			stmt.setBigDecimal(2, item.getAmount());
			stmt.setLong(3, item.getId());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error updating invoice item: " + e.getMessage());
			return false;
		}
	}

	public static boolean delete(long id) {
		String sql = "DELETE FROM Invoice_Item WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting invoice item: " + e.getMessage());
			return false;
		}
	}

}
