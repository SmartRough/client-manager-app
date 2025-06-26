package com.smartrough.app.dao;

import com.smartrough.app.model.ContractAttachment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractAttachmentDAO {

	// SAVE con conexión explícita (para transacción)
	public static long save(Connection conn, ContractAttachment attachment) throws SQLException {
		String sql = "INSERT INTO Contract_Attachment (contract_id, name, extension) VALUES (?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setLong(1, attachment.getContractId());
			stmt.setString(2, attachment.getName());
			stmt.setString(3, attachment.getExtension());
			stmt.executeUpdate();

			ResultSet keys = stmt.getGeneratedKeys();
			if (keys.next()) {
				return keys.getLong(1);
			}
			return -1;
		}
	}

	// SAVE tradicional por fuera de transacción
	public static long save(ContractAttachment attachment) {
		try (Connection conn = Database.connect()) {
			return save(conn, attachment);
		} catch (SQLException e) {
			System.err.println("Error saving contract attachment: " + e.getMessage());
			return -1;
		}
	}

	public static List<ContractAttachment> findByContractId(long contractId) {
		List<ContractAttachment> list = new ArrayList<>();
		String sql = "SELECT * FROM Contract_Attachment WHERE contract_id = ?";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(mapResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Error fetching contract attachments: " + e.getMessage());
		}

		return list;
	}

	// deleteByContractId con conexión explícita
	public static boolean deleteByContractId(Connection conn, long contractId) throws SQLException {
		String sql = "DELETE FROM Contract_Attachment WHERE contract_id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			stmt.executeUpdate();
			return true;
		}
	}

	// delete individual
	public static boolean delete(long id) {
		String sql = "DELETE FROM Contract_Attachment WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting contract attachment: " + e.getMessage());
			return false;
		}
	}

	public static boolean delete(Connection conn, long id) throws SQLException {
		String sql = "DELETE FROM Contract_Attachment WHERE id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		}
	}

	private static ContractAttachment mapResultSet(ResultSet rs) throws SQLException {
		ContractAttachment attachment = new ContractAttachment();
		attachment.setId(rs.getLong("id"));
		attachment.setContractId(rs.getLong("contract_id"));
		attachment.setName(rs.getString("name"));
		attachment.setExtension(rs.getString("extension"));
		return attachment;
	}
}
