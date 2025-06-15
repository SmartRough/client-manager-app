package com.smartrough.app.dao;

import com.smartrough.app.model.ProjectPropertyInfo;

import java.sql.*;

public class ProjectPropertyInfoDAO {

	public static long save(ProjectPropertyInfo info) {
		String[] columns = { "contract_id", "is_home", "is_condo", "is_mfh", "is_commercial", "is_hoa" };
		Object[] values = { info.getContractId(), info.isHome() ? 1 : 0, info.isCondo() ? 1 : 0, info.isMFH() ? 1 : 0,
				info.isCommercial() ? 1 : 0, info.getIsHOA() != null && info.getIsHOA() ? 1 : 0 };
		int[] types = { Types.BIGINT, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER };

		return CRUDHelper.create("Project_Property_Info", columns, values, types);
	}

	public static boolean update(ProjectPropertyInfo info) {
		String sql = """
					UPDATE Project_Property_Info
					SET is_home = ?, is_condo = ?, is_mfh = ?, is_commercial = ?, is_hoa = ?
					WHERE contract_id = ?
				""";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, info.isHome() ? 1 : 0);
			stmt.setInt(2, info.isCondo() ? 1 : 0);
			stmt.setInt(3, info.isMFH() ? 1 : 0);
			stmt.setInt(4, info.isCommercial() ? 1 : 0);
			stmt.setInt(5, info.getIsHOA() != null && info.getIsHOA() ? 1 : 0);
			stmt.setLong(6, info.getContractId());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error updating ProjectPropertyInfo: " + e.getMessage());
			return false;
		}
	}

	public static ProjectPropertyInfo findById(Long id) {
		try (Connection conn = Database.connect();
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Project_Property_Info WHERE id = ?")) {

			stmt.setLong(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					ProjectPropertyInfo info = new ProjectPropertyInfo();
					info.setId(rs.getLong("id"));
					info.setContractId(rs.getLong("contract_id"));
					info.setHome(rs.getBoolean("is_home"));
					info.setCondo(rs.getBoolean("is_condo"));
					info.setMFH(rs.getBoolean("is_mfh"));
					info.setCommercial(rs.getBoolean("is_commercial"));
					info.setIsHOA(rs.getObject("is_hoa") != null ? rs.getBoolean("is_hoa") : null);
					return info;
				}
			}

		} catch (SQLException e) {
			System.err.println("Error retrieving ProjectPropertyInfo: " + e.getMessage());
		}

		return null;
	}

	public static ProjectPropertyInfo findByContractId(long contractId) {
		String sql = "SELECT * FROM Project_Property_Info WHERE contract_id = ?";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				ProjectPropertyInfo info = new ProjectPropertyInfo();
				info.setId(rs.getLong("id"));
				info.setContractId(rs.getLong("contract_id"));
				info.setHome(rs.getInt("is_home") == 1);
				info.setCondo(rs.getInt("is_condo") == 1);
				info.setMFH(rs.getInt("is_mfh") == 1);
				info.setCommercial(rs.getInt("is_commercial") == 1);
				info.setIsHOA(rs.getObject("is_hoa") != null ? rs.getInt("is_hoa") == 1 : null);
				return info;
			}
		} catch (SQLException e) {
			System.err.println("Error fetching ProjectPropertyInfo: " + e.getMessage());
		}
		return null;
	}

	public static boolean deleteByContractId(long contractId) {
		String sql = "DELETE FROM Project_Property_Info WHERE contract_id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting ProjectPropertyInfo: " + e.getMessage());
			return false;
		}
	}
}
