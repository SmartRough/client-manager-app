package com.smartrough.app.dao;

import com.smartrough.app.model.Address;

import java.sql.*;

public class AddressDAO {

	public static Address findById(long id) {
		String sql = "SELECT * FROM Address WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return mapResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching address: " + e.getMessage());
		}
		return null;
	}

	public static Address save(Address address) {
		String sql = "INSERT INTO Address (street, city, state, zip_code) VALUES (?, ?, ?, ?)";
		try (Connection conn = Database.connect();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, address.getStreet());
			stmt.setString(2, address.getCity());
			stmt.setString(3, address.getState());
			stmt.setString(4, address.getZipCode());
			stmt.executeUpdate();

			ResultSet keys = stmt.getGeneratedKeys();
			if (keys.next()) {
				address.setId(keys.getLong(1));
			}
			return address;

		} catch (SQLException e) {
			System.err.println("Error saving address: " + e.getMessage());
			return null;
		}
	}

	private static Address mapResultSet(ResultSet rs) throws SQLException {
		Address address = new Address();
		address.setId(rs.getLong("id"));
		address.setStreet(rs.getString("street"));
		address.setCity(rs.getString("city"));
		address.setState(rs.getString("state"));
		address.setZipCode(rs.getString("zip_code"));
		return address;
	}
}
