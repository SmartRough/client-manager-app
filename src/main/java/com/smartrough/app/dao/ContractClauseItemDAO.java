package com.smartrough.app.dao;

import com.smartrough.app.model.ContractClauseItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractClauseItemDAO {

	public static long save(ContractClauseItem item) {
		String[] columns = { "contract_id", "order_number", "description" };
		Object[] values = { item.getContractId(), item.getOrder(), item.getDescription() };
		int[] types = { Types.BIGINT, Types.INTEGER, Types.VARCHAR };

		return CRUDHelper.create("Contract_Clause_Item", columns, values, types);
	}

	public static List<ContractClauseItem> findByContractId(long contractId) {
		List<ContractClauseItem> list = new ArrayList<>();
		String sql = "SELECT * FROM Contract_Clause_Item WHERE contract_id = ? ORDER BY order_number ASC";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, contractId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				list.add(mapResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Error fetching clause items: " + e.getMessage());
		}

		return list;
	}

	public static boolean deleteByContractId(long contractId) {
		String sql = "DELETE FROM Contract_Clause_Item WHERE contract_id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting clause items by contract: " + e.getMessage());
			return false;
		}
	}

	public static boolean delete(long id) {
		String sql = "DELETE FROM Contract_Clause_Item WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting clause item: " + e.getMessage());
			return false;
		}
	}

	private static ContractClauseItem mapResultSet(ResultSet rs) throws SQLException {
		ContractClauseItem item = new ContractClauseItem();
		item.setId(rs.getLong("id"));
		item.setContractId(rs.getLong("contract_id"));
		item.setOrder(rs.getInt("order_number"));
		item.setDescription(rs.getString("description"));
		return item;
	}
}
