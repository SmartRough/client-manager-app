package com.smartrough.app.dao;

import com.smartrough.app.model.ContractClientInfo;

import java.sql.*;

public class ContractClientInfoDAO {

	public static long save(ContractClientInfo info) {
		String[] columns = { "company_name", "owner_name1", "owner_name2", "email", "address", "home_phone",
				"other_phone" };
		Object[] values = { info.getCompanyName(), info.getOwnerName1(), info.getOwnerName2(), info.getEmail(),
				info.getAddress(), info.getHomePhone(), info.getOtherPhone() };
		int[] types = { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR };

		return CRUDHelper.create("Contract_Client_Info", columns, values, types);
	}

	public static boolean update(ContractClientInfo info) {
		String sql = """
					UPDATE Contract_Client_Info
					SET company_name = ?, owner_name1 = ?, owner_name2 = ?, email = ?, address = ?, home_phone = ?, other_phone = ?
					WHERE id = ?
				""";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, info.getCompanyName());
			stmt.setString(2, info.getOwnerName1());
			stmt.setString(3, info.getOwnerName2());
			stmt.setString(4, info.getEmail());
			stmt.setString(5, info.getAddress());
			stmt.setString(6, info.getHomePhone());
			stmt.setString(7, info.getOtherPhone());
			stmt.setLong(8, info.getId());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error updating ContractClientInfo: " + e.getMessage());
			return false;
		}
	}

	public static ContractClientInfo findById(long id) {
		String sql = "SELECT * FROM Contract_Client_Info WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				ContractClientInfo info = new ContractClientInfo();
				info.setId(rs.getLong("id"));
				info.setCompanyName(rs.getString("company_name"));
				info.setOwnerName1(rs.getString("owner_name1"));
				info.setOwnerName2(rs.getString("owner_name2"));
				info.setEmail(rs.getString("email"));
				info.setAddress(rs.getString("address"));
				info.setHomePhone(rs.getString("home_phone"));
				info.setOtherPhone(rs.getString("other_phone"));
				return info;
			}
		} catch (SQLException e) {
			System.err.println("Error fetching ContractClientInfo: " + e.getMessage());
		}
		return null;
	}

	public static boolean delete(long id) {
		String sql = "DELETE FROM Contract_Client_Info WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting ContractClientInfo: " + e.getMessage());
			return false;
		}
	}
}
