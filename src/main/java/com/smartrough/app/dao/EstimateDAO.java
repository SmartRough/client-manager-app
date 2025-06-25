package com.smartrough.app.dao;

import com.smartrough.app.model.Estimate;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EstimateDAO {

	public static long save(Estimate estimate) {
		String[] columns = { "date", "company_id", "customer_id", "approved_by", "job_description", "total",
				"image_names" };
		Object[] values = { estimate.getDate() != null ? estimate.getDate().toString() : null, estimate.getCompanyId(),
				estimate.getCustomerId(), estimate.getApproved_by(), estimate.getJobDescription(), estimate.getTotal(),
				String.join(",", estimate.getImageNames()) };
		int[] types = { Types.VARCHAR, Types.BIGINT, Types.BIGINT, Types.VARCHAR, Types.VARCHAR, Types.DECIMAL,
				Types.VARCHAR };

		return CRUDHelper.create("Estimate", columns, values, types);
	}

	public static boolean update(Estimate estimate) {
		String sql = """
					UPDATE Estimate
					SET date=?, company_id=?, customer_id=?, approved_by=?, job_description=?, total=?, image_names=?
					WHERE id=?
				""";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, estimate.getDate() != null ? estimate.getDate().toString() : null);
			stmt.setLong(2, estimate.getCompanyId());
			stmt.setLong(3, estimate.getCustomerId());
			stmt.setString(4, estimate.getApproved_by());
			stmt.setString(5, estimate.getJobDescription());
			stmt.setBigDecimal(6, estimate.getTotal());
			stmt.setString(7, String.join(",", estimate.getImageNames()));
			stmt.setLong(8, estimate.getId());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error updating estimate: " + e.getMessage());
			return false;
		}
	}

	public static List<Estimate> findAll() {
		List<Estimate> list = new ArrayList<>();
		String sql = "SELECT * FROM Estimate ORDER BY date ASC";

		try (Connection conn = Database.connect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				list.add(mapResultSet(rs));
			}
		} catch (SQLException e) {
			System.err.println("Error fetching estimates: " + e.getMessage());
		}
		return list;
	}

	public static Estimate findById(long id) {
		String sql = "SELECT * FROM Estimate WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return mapResultSet(rs);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching estimate: " + e.getMessage());
		}
		return null;
	}

	public static boolean delete(long id) {
		String sql = "DELETE FROM Estimate WHERE id=?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting estimate: " + e.getMessage());
			return false;
		}
	}

	private static Estimate mapResultSet(ResultSet rs) throws SQLException {
		Estimate e = new Estimate();
		e.setId(rs.getLong("id"));

		try {
			String dateStr = rs.getString("date");
			if (dateStr != null && !dateStr.isBlank()) {
				e.setDate(LocalDate.parse(dateStr)); // yyyy-MM-dd
			}
		} catch (Exception ex) {
			System.err.println(">> ERROR parsing estimate date: " + ex.getMessage());
			ex.printStackTrace();
			e.setDate(null);
		}

		e.setCompanyId(rs.getLong("company_id"));
		e.setCustomerId(rs.getLong("customer_id"));
		e.setApproved_by(rs.getString("approved_by"));
		e.setJobDescription(rs.getString("job_description"));
		e.setTotal(rs.getBigDecimal("total"));

		String imageCsv = rs.getString("image_names");
		List<String> imageNames = (imageCsv != null && !imageCsv.isBlank()) ? Arrays.asList(imageCsv.split(","))
				: new ArrayList<>();
		e.setImageNames(imageNames);

		e.setItems(EstimateItemDAO.findByEstimateId(e.getId()));
		return e;
	}
}
