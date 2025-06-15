package com.smartrough.app.dao;

import com.smartrough.app.model.Signature;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SignatureDAO {

	public static long save(Signature signature) {
		String[] columns = { "contract_id", "name", "role", "date_signed", "image_name" };
		Object[] values = { signature.getContractId(), signature.getName(), signature.getRole(),
				signature.getDateSigned() != null ? signature.getDateSigned().toString() : null,
				signature.getImageName() };
		int[] types = { Types.BIGINT, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };

		return CRUDHelper.create("Signature", columns, values, types);
	}

	public static List<Signature> findByContractId(long contractId) {
		List<Signature> signatures = new ArrayList<>();
		String sql = "SELECT * FROM Signature WHERE contract_id = ?";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Signature s = new Signature();
				s.setId(rs.getLong("id"));
				s.setContractId(rs.getLong("contract_id"));
				s.setName(rs.getString("name"));
				s.setRole(rs.getString("role"));
				s.setImageName(rs.getString("image_name"));

				String dateStr = rs.getString("date_signed");
				if (dateStr != null && !dateStr.isBlank()) {
					s.setDateSigned(LocalDate.parse(dateStr));
				}

				signatures.add(s);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching signatures: " + e.getMessage());
		}
		return signatures;
	}

	public static boolean deleteByContractId(long contractId) {
		String sql = "DELETE FROM Signature WHERE contract_id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting signatures: " + e.getMessage());
			return false;
		}
	}
}
