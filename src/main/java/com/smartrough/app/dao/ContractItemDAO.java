package com.smartrough.app.dao;

import com.smartrough.app.model.ContractItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractItemDAO {

	public static long save(ContractItem item) {
		String[] columns = { "contract_id", "order_number", "description" };
		Object[] values = { item.getContractId(), item.getOrder(), item.getDescription() };
		int[] types = { Types.BIGINT, Types.INTEGER, Types.VARCHAR };

		return CRUDHelper.create("Contract_Item", columns, values, types);
	}

	public static boolean update(ContractItem item) {
		String sql = """
					UPDATE Contract_Item
					SET contract_id = ?, order_number = ?, description = ?
					WHERE id = ?
				""";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, item.getContractId());
			stmt.setInt(2, item.getOrder());
			stmt.setString(3, item.getDescription());
			stmt.setLong(4, item.getId());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error updating ContractItem: " + e.getMessage());
			return false;
		}
	}

	public static List<ContractItem> findByContractId(long contractId) {
		List<ContractItem> items = new ArrayList<>();
		String sql = "SELECT * FROM Contract_Item WHERE contract_id = ? ORDER BY order_number ASC";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ContractItem item = new ContractItem();
				item.setId(rs.getLong("id"));
				item.setContractId(rs.getLong("contract_id"));
				item.setOrder(rs.getInt("order_number"));
				item.setDescription(rs.getString("description"));
				items.add(item);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching contract items: " + e.getMessage());
		}
		return items;
	}

	public static boolean deleteByContractId(long contractId) {
		String sql = "DELETE FROM Contract_Item WHERE contract_id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting contract items: " + e.getMessage());
			return false;
		}
	}

	public static boolean delete(long id) {
		String sql = "DELETE FROM Contract_Item WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting contract item: " + e.getMessage());
			return false;
		}
	}
}
