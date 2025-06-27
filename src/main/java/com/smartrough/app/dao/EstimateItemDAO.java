package com.smartrough.app.dao;

import com.smartrough.app.model.EstimateItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstimateItemDAO {

	public static long save(EstimateItem item) {
		String[] columns = { "estimate_id", "description", "\"order\"" };
		Object[] values = { item.getEstimateId(), item.getDescription(), item.getOrder() };
		int[] types = { Types.BIGINT, Types.VARCHAR, Types.INTEGER };

		return CRUDHelper.create("Estimate_Item", columns, values, types);
	}

	public static List<EstimateItem> findByEstimateId(long estimateId) {
		List<EstimateItem> items = new ArrayList<>();
		String sql = "SELECT * FROM Estimate_Item WHERE estimate_id = ? ORDER BY \"order\" ASC";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, estimateId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				EstimateItem item = new EstimateItem();
				item.setId(rs.getLong("id"));
				item.setEstimateId(rs.getLong("estimate_id"));
				item.setDescription(rs.getString("description"));
				item.setOrder(rs.getInt("order"));
				items.add(item);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching estimate items: " + e.getMessage());
		}
		return items;
	}

	public static boolean update(EstimateItem item) {
		String sql = "UPDATE Estimate_Item SET description=?, \"order\"=? WHERE id=?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, item.getDescription());
			stmt.setInt(2, item.getOrder());
			stmt.setLong(3, item.getId());
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error updating estimate item: " + e.getMessage());
			return false;
		}
	}

	public static boolean deleteById(long id) {
		String sql = "DELETE FROM Estimate_Item WHERE id=?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting estimate item: " + e.getMessage());
			return false;
		}
	}

	public static boolean delete(long estimateId) {
		String sql = "DELETE FROM Estimate_Item WHERE estimate_id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, estimateId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting estimate items: " + e.getMessage());
			return false;
		}
	}
}
