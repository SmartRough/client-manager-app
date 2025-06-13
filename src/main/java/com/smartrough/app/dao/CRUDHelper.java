package com.smartrough.app.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.smartrough.app.model.Address;

public class CRUDHelper {

	private static final Logger LOGGER = Logger.getLogger(CRUDHelper.class.getName());

	public static Object read(String tableName, String fieldName, int fieldDataType, String indexFieldName,
			int indexDataType, Object index) {
		String query = String.format("SELECT %s FROM %s WHERE %s = ?", fieldName, tableName, indexFieldName);

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setObject(1, index, indexDataType);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				switch (fieldDataType) {
				case Types.INTEGER:
					return rs.getInt(fieldName);
				case Types.VARCHAR:
					return rs.getString(fieldName);
				default:
					throw new IllegalArgumentException("Unsupported field type.");
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, LocalDateTime.now() + ": Failed to read from " + tableName, e);
		}
		return null;
	}

	public static int update(String tableName, String[] columns, Object[] values, int[] types, String indexFieldName,
			int indexDataType, Object index) {
		StringBuilder query = new StringBuilder("UPDATE " + tableName + " SET ");
		for (int i = 0; i < columns.length; i++) {
			query.append(columns[i]).append(" = ?");
			if (i < columns.length - 1)
				query.append(", ");
		}
		query.append(" WHERE ").append(indexFieldName).append(" = ?");

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(query.toString())) {
			for (int i = 0; i < values.length; i++) {
				stmt.setObject(i + 1, values[i], types[i]);
			}
			stmt.setObject(values.length + 1, index, indexDataType);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, LocalDateTime.now() + ": Failed to update " + tableName, e);
			return -1;
		}
	}

	public static long create(String tableName, String[] columns, Object[] values, int[] types) {
		StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
		for (int i = 0; i < columns.length; i++) {
			query.append(columns[i]);
			if (i < columns.length - 1)
				query.append(", ");
		}
		query.append(") VALUES (");
		query.append("?,".repeat(columns.length));
		query.setLength(query.length() - 1); // remove last comma
		query.append(")");

		try (Connection conn = Database.connect();
				PreparedStatement stmt = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
			for (int i = 0; i < values.length; i++) {
				stmt.setObject(i + 1, values[i], types[i]);
			}
			int affectedRows = stmt.executeUpdate();
			if (affectedRows > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next())
					return rs.getLong(1);
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, LocalDateTime.now() + ": Failed to insert into " + tableName, e);
		}
		return -1;
	}

	public static int delete(String tableName, int id) {
		String sql = "DELETE FROM " + tableName + " WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, LocalDateTime.now() + ": Failed to delete from " + tableName, e);
			return -1;
		}
	}

	public static List<Address> findAllAddresses() {
		List<Address> addresses = new ArrayList<>();
		String query = "SELECT * FROM Address";

		try (Connection conn = Database.connect();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Address addr = new Address();
				addr.setId(rs.getLong("id"));
				addr.setStreet(rs.getString("street"));
				addr.setCity(rs.getString("city"));
				addr.setState(rs.getString("state"));
				addr.setZipCode(rs.getString("zip_code"));
				addresses.add(addr);
			}

		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, LocalDateTime.now() + ": Failed to load addresses", e);
		}
		return addresses;
	}

}
