package com.smartrough.app.dao;

import com.smartrough.app.model.ContractItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractItemDAO {

	// Método SAVE con conexión explícita
	public static long save(Connection conn, ContractItem item) throws SQLException {
		String sql = "INSERT INTO Contract_Item (contract_id, description) VALUES (?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setLong(1, item.getContractId());
			stmt.setString(2, item.getDescription());
			stmt.executeUpdate();

			ResultSet keys = stmt.getGeneratedKeys();
			if (keys.next()) {
				return keys.getLong(1);
			}
			return -1;
		}
	}

	// Método SAVE para casos sueltos fuera de transacción
	public static long save(ContractItem item) {
		try (Connection conn = Database.connect()) {
			return save(conn, item);
		} catch (SQLException e) {
			System.err.println("Error saving contract item: " + e.getMessage());
			return -1;
		}
	}

	public static List<ContractItem> findByContractId(long contractId) {
		List<ContractItem> list = new ArrayList<>();
		String sql = "SELECT * FROM Contract_Item WHERE contract_id = ?";

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

	// deleteByContractId con conexión explícita para uso transaccional
	public static boolean deleteByContractId(Connection conn, long contractId) throws SQLException {
		String sql = "DELETE FROM Contract_Item WHERE contract_id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			stmt.executeUpdate();
			return true;
		}
	}

	// delete individual sin transacción
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

	private static ContractItem mapResultSet(ResultSet rs) throws SQLException {
		ContractItem item = new ContractItem();
		item.setId(rs.getLong("id"));
		item.setContractId(rs.getLong("contract_id"));
		item.setDescription(rs.getString("description"));
		return item;
	}
}
