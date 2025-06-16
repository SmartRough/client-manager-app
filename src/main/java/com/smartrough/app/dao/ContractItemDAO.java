package com.smartrough.app.dao;

import com.smartrough.app.model.ContractItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractItemDAO {

	public static long save(ContractItem item) {
		String[] columns = { "contract_id", "description" };
		Object[] values = { item.getContractId(), item.getDescription() };
		int[] types = { Types.BIGINT, Types.VARCHAR };

		return CRUDHelper.create("ContractItem", columns, values, types);
	}

	public static List<ContractItem> findByContractId(long contractId) {
		List<ContractItem> list = new ArrayList<>();
		String sql = "SELECT * FROM ContractItem WHERE contract_id = ?";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(mapResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Error fetching contract items: " + e.getMessage());
		}

		return list;
	}

	public static boolean deleteByContractId(long contractId) {
		String sql = "DELETE FROM ContractItem WHERE contract_id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting contract items by contract: " + e.getMessage());
			return false;
		}
	}

	public static boolean delete(long id) {
		String sql = "DELETE FROM ContractItem WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting contract item: " + e.getMessage());
			return false;
		}
	}

	private static ContractItem mapResultSet(ResultSet rs) throws SQLException {
		ContractItem item = new ContractItem();
		item.setId(rs.getLong("id"));
		item.setContractId(rs.getLong("contract_id"));
		item.setDescription(rs.getString("description"));
		return item;
	}
}
