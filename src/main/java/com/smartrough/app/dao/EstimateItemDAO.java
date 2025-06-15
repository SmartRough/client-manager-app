package com.smartrough.app.dao;

import com.smartrough.app.model.EstimateItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstimateItemDAO {

	public static long save(EstimateItem item) {
		String[] columns = { "estimate_id", "description" };
		Object[] values = { item.getEstimateId(), item.getDescription() };
		int[] types = { Types.BIGINT, Types.VARCHAR };

		return CRUDHelper.create("Estimate_Item", columns, values, types);
	}

	public static List<EstimateItem> findByEstimateId(long estimateId) {
		List<EstimateItem> items = new ArrayList<>();
		String sql = "SELECT * FROM Estimate_Item WHERE estimate_id = ?";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, estimateId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				EstimateItem item = new EstimateItem();
				item.setId(rs.getLong("id"));
				item.setEstimateId(rs.getLong("estimate_id"));
				item.setDescription(rs.getString("description"));
				items.add(item);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching estimate items: " + e.getMessage());
		}
		return items;
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
