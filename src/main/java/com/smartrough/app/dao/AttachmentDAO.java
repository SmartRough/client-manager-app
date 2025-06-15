package com.smartrough.app.dao;

import com.smartrough.app.model.Attachment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttachmentDAO {

	public static long save(Attachment attachment) {
		String[] columns = { "contract_id", "file_name", "file_type" };
		Object[] values = { attachment.getContractId(), attachment.getFileName(), attachment.getFileType() };
		int[] types = { Types.BIGINT, Types.VARCHAR, Types.VARCHAR };

		return CRUDHelper.create("Attachment", columns, values, types);
	}

	public static boolean update(Attachment attachment) {
		String sql = """
					UPDATE Attachment
					SET contract_id = ?, file_name = ?, file_type = ?
					WHERE id = ?
				""";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, attachment.getContractId());
			stmt.setString(2, attachment.getFileName());
			stmt.setString(3, attachment.getFileType());
			stmt.setLong(4, attachment.getId());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error updating attachment: " + e.getMessage());
			return false;
		}
	}

	public static List<Attachment> findByContractId(long contractId) {
		List<Attachment> attachments = new ArrayList<>();
		String sql = "SELECT * FROM Attachment WHERE contract_id = ?";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Attachment a = new Attachment();
				a.setId(rs.getLong("id"));
				a.setContractId(rs.getLong("contract_id"));
				a.setFileName(rs.getString("file_name"));
				a.setFileType(rs.getString("file_type"));
				attachments.add(a);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching attachments: " + e.getMessage());
		}
		return attachments;
	}

	public static boolean deleteByContractId(long contractId) {
		String sql = "DELETE FROM Attachment WHERE contract_id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting attachments by contract: " + e.getMessage());
			return false;
		}
	}

	public static boolean delete(long id) {
		String sql = "DELETE FROM Attachment WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting attachment: " + e.getMessage());
			return false;
		}
	}
}
