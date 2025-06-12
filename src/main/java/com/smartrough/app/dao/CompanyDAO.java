package com.smartrough.app.dao;

import com.smartrough.app.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {

	public static long saveCompany(Company company) {
		String[] columns = { "name", "representative", "phone", "email", "address_id", "is_own_company" };
		Object[] values = { company.getName(), company.getRepresentative(), company.getPhone(), company.getEmail(),
				company.getAddressId(), company.isOwnCompany() ? 1 : 0 };
		int[] types = { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER };

		return CRUDHelper.create("Company", columns, values, types);
	}

	public static List<Company> findAll() {
		List<Company> list = new ArrayList<>();
		String sql = "SELECT * FROM Company";

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
				list.add(c);
			}

		} catch (SQLException e) {
			System.err.println("Error fetching companies: " + e.getMessage());
		}

		return list;
	}
}
