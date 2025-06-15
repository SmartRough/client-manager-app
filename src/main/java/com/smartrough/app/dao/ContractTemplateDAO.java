package com.smartrough.app.dao;

import com.smartrough.app.model.Company;
import com.smartrough.app.model.ContractTemplate;
import com.smartrough.app.model.StandardClause;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractTemplateDAO {

	private StandardClauseDAO clauseDAO = new StandardClauseDAO();

	public List<ContractTemplate> findAll() {
		String sql = "SELECT * FROM Contract_Template ORDER BY name ASC";
		List<ContractTemplate> list = new ArrayList<>();

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(map(rs));
			}
		} catch (SQLException e) {
			System.err.println("Error fetching contract templates: " + e.getMessage());
		}
		return list;
	}

	public static ContractTemplate findById(Long id) {
		String sql = "SELECT * FROM Contract_Template WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return map(rs);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching contract template by ID: " + e.getMessage());
		}
		return null;
	}

	public void insert(ContractTemplate template) {
		String sql = "INSERT INTO Contract_Template (name, legal_notice, contractor_id) VALUES (?, ?, ?)";

		try (Connection conn = Database.connect();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, template.getName());
			stmt.setString(2, template.getLegalNotice());
			stmt.setLong(3, template.getContractor().getId());
			stmt.executeUpdate();

			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				template.setId(rs.getLong(1));
			}

			// Insert clauses
			if (template.getClauses() != null) {
				for (StandardClause clause : template.getClauses()) {
					clause.setTemplateId(template.getId());
					clauseDAO.insert(clause);
				}
			}

		} catch (SQLException e) {
			System.err.println("Error inserting contract template: " + e.getMessage());
		}
	}

	public void update(ContractTemplate template) {
		String sql = "UPDATE Contract_Template SET name = ?, legal_notice = ?, contractor_id = ? WHERE id = ?";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, template.getName());
			stmt.setString(2, template.getLegalNotice());
			stmt.setLong(3, template.getContractor().getId());
			stmt.setLong(4, template.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error updating contract template: " + e.getMessage());
		}
	}

	public void delete(Long id) {
		String sql = "DELETE FROM Contract_Template WHERE id = ?";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error deleting contract template: " + e.getMessage());
		}
	}

	private static ContractTemplate map(ResultSet rs) throws SQLException {
		ContractTemplate template = new ContractTemplate();
		template.setId(rs.getLong("id"));
		template.setName(rs.getString("name"));
		template.setLegalNotice(rs.getString("legal_notice"));

		Long contractorId = rs.getLong("contractor_id");
		Company contractor = CompanyDAO.findById(contractorId);
		template.setContractor(contractor);

		List<StandardClause> clauses = StandardClauseDAO.findByTemplateId(template.getId());
		template.setClauses(clauses);

		return template;
	}
}
