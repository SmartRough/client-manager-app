package com.smartrough.app.dao;

import com.smartrough.app.enums.ContractStatus;
import com.smartrough.app.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContractDAO {

	public static Contract findById(Long id) {
		String sql = "SELECT * FROM Contract WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return map(rs);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching Contract: " + e.getMessage());
		}
		return null;
	}

	public static List<Contract> findAll() {
		List<Contract> contracts = new ArrayList<>();
		String sql = "SELECT * FROM Contract";
		try (Connection conn = Database.connect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				contracts.add(map(rs));
			}
		} catch (SQLException e) {
			System.err.println("Error fetching contracts: " + e.getMessage());
		}
		return contracts;
	}

	public static void save(Contract contract) {
		String sql = "INSERT INTO Contract (contract_number, contract_date, template_id, client_info_id, property_info_id, financial_info_id, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = Database.connect();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, contract.getContractNumber());
			stmt.setDate(2, contract.getContractDate() != null ? Date.valueOf(contract.getContractDate()) : null);
			stmt.setLong(3, contract.getTemplate().getId());
			stmt.setLong(4, contract.getClientInfo().getId());
			stmt.setLong(5, contract.getPropertyInfo().getId());
			stmt.setLong(6, contract.getFinancialInfo().getId());
			stmt.setString(7, contract.getStatus().name());

			stmt.executeUpdate();

			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					contract.setId(generatedKeys.getLong(1));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error saving Contract: " + e.getMessage());
		}
	}

	public static void update(Contract contract) {
		String sql = "UPDATE Contract SET contract_number = ?, contract_date = ?, template_id = ?, client_info_id = ?, property_info_id = ?, financial_info_id = ?, status = ? WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, contract.getContractNumber());
			stmt.setDate(2, contract.getContractDate() != null ? Date.valueOf(contract.getContractDate()) : null);
			stmt.setLong(3, contract.getTemplate().getId());
			stmt.setLong(4, contract.getClientInfo().getId());
			stmt.setLong(5, contract.getPropertyInfo().getId());
			stmt.setLong(6, contract.getFinancialInfo().getId());
			stmt.setString(7, contract.getStatus().name());
			stmt.setLong(8, contract.getId());

			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error updating Contract: " + e.getMessage());
		}
	}

	public static void delete(Long id) {
		String sql = "DELETE FROM Contract WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setLong(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error deleting Contract: " + e.getMessage());
		}
	}

	private static Contract map(ResultSet rs) throws SQLException {
		Contract contract = new Contract();
		Long id = rs.getLong("id");
		contract.setId(id);
		contract.setContractNumber(rs.getString("contract_number"));

		Date contractDate = rs.getDate("contract_date");
		if (contractDate != null) {
			contract.setContractDate(contractDate.toLocalDate());
		}

		contract.setTemplate(ContractTemplateDAO.findById(rs.getLong("template_id")));
		contract.setClientInfo(ContractClientInfoDAO.findById(rs.getLong("client_info_id")));
		contract.setPropertyInfo(ProjectPropertyInfoDAO.findById(rs.getLong("property_info_id")));
		contract.setFinancialInfo(ContractFinancialInfoDAO.findById(rs.getLong("financial_info_id")));

		String statusStr = rs.getString("status");
		if (statusStr != null) {
			contract.setStatus(ContractStatus.valueOf(statusStr));
		}

		contract.setDescriptionItems(ContractItemDAO.findByContractId(id));
		contract.setClauseStatuses(ContractStandardClauseStatusDAO.findByContractId(id));
		contract.setAttachments(AttachmentDAO.findByContractId(id));
		contract.setSignatures(SignatureDAO.findByContractId(id));

		return contract;
	}
}
