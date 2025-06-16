package com.smartrough.app.dao;

import com.smartrough.app.model.ContractAttachment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractAttachmentDAO {

	public static long save(ContractAttachment attachment) {
		String[] columns = { "contract_id", "name", "extension" };
		Object[] values = { attachment.getContractId(), attachment.getName(), attachment.getExtension() };
		int[] types = { Types.BIGINT, Types.VARCHAR, Types.VARCHAR };

		return CRUDHelper.create("ContractAttachment", columns, values, types);
	}

	public static List<ContractAttachment> findByContractId(long contractId) {
		List<ContractAttachment> list = new ArrayList<>();
		String sql = "SELECT * FROM ContractAttachment WHERE contract_id = ?";

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

	public static boolean deleteByContractId(long contractId) {
		String sql = "DELETE FROM ContractAttachment WHERE contract_id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting attachments by contract: " + e.getMessage());
			return false;
		}
	}

	public static boolean delete(long id) {
		String sql = "DELETE FROM ContractAttachment WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting contract attachment: " + e.getMessage());
			return false;
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
