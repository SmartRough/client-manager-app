package com.smartrough.app.dao;

import com.smartrough.app.model.InvoiceItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceItemDAO {

	public static long save(InvoiceItem item) {
		String[] columns = { "invoice_id", "description", "amount", "\"order\"" };
		Object[] values = { item.getInvoiceId(), item.getDescription(), item.getAmount(), item.getOrder() };
		int[] types = { Types.BIGINT, Types.VARCHAR, Types.DECIMAL, Types.INTEGER };
		return CRUDHelper.create("Invoice_Item", columns, values, types);
	}

	public static List<InvoiceItem> findByInvoiceId(long invoiceId) {
		List<InvoiceItem> items = new ArrayList<>();
		String sql = "SELECT * FROM Invoice_Item WHERE invoice_id = ? ORDER BY \"order\" ASC";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, invoiceId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				InvoiceItem item = new InvoiceItem();
				item.setId(rs.getLong("id"));
				item.setInvoiceId(rs.getLong("invoice_id"));
				item.setDescription(rs.getString("description"));
				item.setAmount(rs.getBigDecimal("amount"));
				item.setOrder(rs.getInt("order"));
				items.add(item);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching invoice items: " + e.getMessage());
		}

		return items;
	}

	public static boolean update(InvoiceItem item) {
		String sql = "UPDATE Invoice_Item SET description = ?, amount = ?, \"order\" = ? WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, item.getDescription());
			stmt.setBigDecimal(2, item.getAmount());
			stmt.setInt(3, item.getOrder());
			stmt.setLong(4, item.getId());
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

	public static boolean deleteByInvoiceId(long invoiceId) {
		String sql = "DELETE FROM Invoice_Item WHERE invoice_id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, invoiceId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting items by invoice_id: " + e.getMessage());
			return false;
		}
	}

}
