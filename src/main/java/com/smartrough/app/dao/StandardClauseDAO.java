package com.smartrough.app.dao;

import com.smartrough.app.model.StandardClause;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StandardClauseDAO {

	public static List<StandardClause> findByTemplateId(Long templateId) {
		String sql = "SELECT * FROM Standard_Clause WHERE template_id = ? ORDER BY clause_order ASC";
		List<StandardClause> list = new ArrayList<>();

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, templateId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(map(rs));
			}
		} catch (SQLException e) {
			System.err.println("Error fetching Standard Clauses: " + e.getMessage());
		}
		return list;
	}

	public void insert(StandardClause clause) {
		String sql = "INSERT INTO Standard_Clause (template_id, clause_order, title, content, active) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, clause.getTemplateId());
			stmt.setInt(2, clause.getOrder());
			stmt.setString(3, clause.getTitle());
			stmt.setString(4, clause.getContent());
			stmt.setBoolean(5, clause.isActive());
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error inserting Standard Clause: " + e.getMessage());
		}
	}

	public void update(StandardClause clause) {
		String sql = "UPDATE Standard_Clause SET clause_order = ?, title = ?, content = ?, active = ? WHERE id = ?";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, clause.getOrder());
			stmt.setString(2, clause.getTitle());
			stmt.setString(3, clause.getContent());
			stmt.setBoolean(4, clause.isActive());
			stmt.setLong(5, clause.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error updating Standard Clause: " + e.getMessage());
		}
	}

	public void delete(Long id) {
		String sql = "DELETE FROM Standard_Clause WHERE id = ?";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error deleting Standard Clause: " + e.getMessage());
		}
	}

	private static StandardClause map(ResultSet rs) throws SQLException {
		StandardClause clause = new StandardClause();
		clause.setId(rs.getLong("id"));
		clause.setTemplateId(rs.getLong("template_id"));
		clause.setOrder(rs.getInt("clause_order"));
		clause.setTitle(rs.getString("title"));
		clause.setContent(rs.getString("content"));
		clause.setActive(rs.getBoolean("active"));
		return clause;
	}
}
