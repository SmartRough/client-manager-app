package com.smartrough.app.dao;

import com.smartrough.app.enums.CompanyType;
import com.smartrough.app.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {

	public static long saveCompany(Company company) {
		String[] columns = { "name", "representative", "phone", "email", "address_id", "is_own_company", "type",
				"license" };
		Object[] values = { company.getName(), company.getRepresentative(), company.getPhone(), company.getEmail(),
				company.getAddressId(), company.isOwnCompany() ? 1 : 0, company.getType().name(),
				company.getLicense() };
		int[] types = { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER,
				Types.VARCHAR, Types.VARCHAR };

		return CRUDHelper.create("Company", columns, values, types);
	}

	public static Company findOwnCompany() {
		String sql = "SELECT * FROM Company WHERE is_own_company = TRUE LIMIT 1";
		try (Connection conn = Database.connect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			if (rs.next()) {
				return mapResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching own company: " + e.getMessage());
		}
		return null;
	}

	public static Company findById(long id) {
		String sql = "SELECT * FROM Company WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return mapResultSet(rs);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching company by ID: " + e.getMessage());
		}
		return null;
	}

	public static List<Company> findAll() {
		List<Company> list = new ArrayList<>();
		String sql = "SELECT * FROM Company WHERE is_own_company = FALSE";

		try (Connection conn = Database.connect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Company c = new Company();
				c.setId(rs.getLong("id"));
				c.setName(rs.getString("name"));
				c.setRepresentative(rs.getString("representative"));
				c.setPhone(rs.getString("phone"));
				c.setEmail(rs.getString("email"));
				c.setAddressId(rs.getLong("address_id"));
				c.setOwnCompany(rs.getInt("is_own_company") == 1);
				c.setType(CompanyType.valueOf(rs.getString("type")));
				c.setLicense(rs.getString("license"));
				list.add(c);
			}

		} catch (SQLException e) {
			System.err.println("Error fetching companies: " + e.getMessage());
		}

		return list;
	}

	public static boolean updateCompany(Company company) {
		String sql = "UPDATE Company SET name=?, representative=?, phone=?, email=?, address_id=?, is_own_company=?, type=?, license=? WHERE id=?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, company.getName());
			stmt.setString(2, company.getRepresentative());
			stmt.setString(3, company.getPhone());
			stmt.setString(4, company.getEmail());
			stmt.setLong(5, company.getAddressId());
			stmt.setInt(6, company.isOwnCompany() ? 1 : 0);
			stmt.setString(7, company.getType().name());
			stmt.setString(8, company.getLicense());
			stmt.setLong(9, company.getId());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean deleteById(long id) {
		String sql = "DELETE FROM Company WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting company: " + e.getMessage());
			return false;
		}
	}

	private static Company mapResultSet(ResultSet rs) throws SQLException {
		Company c = new Company();
		c.setId(rs.getLong("id"));
		c.setName(rs.getString("name"));
		c.setRepresentative(rs.getString("representative"));
		c.setPhone(rs.getString("phone"));
		c.setEmail(rs.getString("email"));
		c.setAddressId(rs.getLong("address_id"));
		c.setOwnCompany(rs.getInt("is_own_company") == 1);
		c.setType(CompanyType.valueOf(rs.getString("type")));
		c.setLicense(rs.getString("license"));
		return c;
	}

}
