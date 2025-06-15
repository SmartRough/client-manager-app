package com.smartrough.app.dao;

import com.smartrough.app.model.ContractStandardClauseStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractStandardClauseStatusDAO {

	public static boolean save(ContractStandardClauseStatus status) {
		String sql = """
					INSERT INTO Contract_Standard_Clause_Status (contract_id, clause_id, initialed)
					VALUES (?, ?, ?)
				""";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, status.getContractId());
			stmt.setLong(2, status.getClauseId());
			stmt.setBoolean(3, status.isInitialed());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error saving clause status: " + e.getMessage());
			return false;
		}
	}

	public static List<ContractStandardClauseStatus> findByContractId(long contractId) {
		List<ContractStandardClauseStatus> list = new ArrayList<>();
		String sql = "SELECT * FROM Contract_Standard_Clause_Status WHERE contract_id = ?";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				ContractStandardClauseStatus status = new ContractStandardClauseStatus();
				status.setContractId(rs.getLong("contract_id"));
				status.setClauseId(rs.getLong("clause_id"));
				status.setInitialed(rs.getBoolean("initialed"));
				list.add(status);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching clause statuses: " + e.getMessage());
		}
		return list;
	}

	public static boolean deleteByContractId(long contractId) {
		String sql = "DELETE FROM Contract_Standard_Clause_Status WHERE contract_id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting clause statuses: " + e.getMessage());
			return false;
		}
	}
}
