package com.smartrough.app.dao;

import com.smartrough.app.model.ContractFinancialInfo;

import java.sql.*;

public class ContractFinancialInfoDAO {

	public static long save(ContractFinancialInfo info) {
		String[] columns = { "contract_id", "total_price", "deposit", "balance_due_upon_completion",
				"amount_financed" };
		Object[] values = { info.getContractId(), info.getTotalPrice(), info.getDeposit(),
				info.getBalanceDueUponCompletion(), info.getAmountFinanced() };
		int[] types = { Types.BIGINT, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL, Types.DECIMAL };

		return CRUDHelper.create("Contract_Financial_Info", columns, values, types);
	}

	public static boolean update(ContractFinancialInfo info) {
		String sql = """
					UPDATE Contract_Financial_Info
					SET total_price = ?, deposit = ?, balance_due_upon_completion = ?, amount_financed = ?
					WHERE contract_id = ?
				""";

		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setBigDecimal(1, info.getTotalPrice());
			stmt.setBigDecimal(2, info.getDeposit());
			stmt.setBigDecimal(3, info.getBalanceDueUponCompletion());
			stmt.setBigDecimal(4, info.getAmountFinanced());
			stmt.setLong(5, info.getContractId());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error updating ContractFinancialInfo: " + e.getMessage());
			return false;
		}
	}

	public static ContractFinancialInfo findById(Long id) {
		try (Connection conn = Database.connect();
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Contract_Financial_Info WHERE id = ?")) {

			stmt.setLong(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					ContractFinancialInfo info = new ContractFinancialInfo();
					info.setId(rs.getLong("id"));
					info.setContractId(rs.getLong("contract_id"));
					info.setTotalPrice(rs.getBigDecimal("total_price"));
					info.setDeposit(rs.getBigDecimal("deposit"));
					info.setBalanceDueUponCompletion(rs.getBigDecimal("balance_due_upon_completion"));
					info.setAmountFinanced(rs.getBigDecimal("amount_financed"));
					return info;
				}
			}

		} catch (SQLException e) {
			System.err.println("Error retrieving ContractFinancialInfo: " + e.getMessage());
		}

		return null;
	}

	public static ContractFinancialInfo findByContractId(long contractId) {
		String sql = "SELECT * FROM Contract_Financial_Info WHERE contract_id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				ContractFinancialInfo info = new ContractFinancialInfo();
				info.setId(rs.getLong("id"));
				info.setContractId(rs.getLong("contract_id"));
				info.setTotalPrice(rs.getBigDecimal("total_price"));
				info.setDeposit(rs.getBigDecimal("deposit"));
				info.setBalanceDueUponCompletion(rs.getBigDecimal("balance_due_upon_completion"));
				info.setAmountFinanced(rs.getBigDecimal("amount_financed"));
				return info;
			}
		} catch (SQLException e) {
			System.err.println("Error fetching ContractFinancialInfo: " + e.getMessage());
		}
		return null;
	}

	public static boolean deleteByContractId(long contractId) {
		String sql = "DELETE FROM Contract_Financial_Info WHERE contract_id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, contractId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting ContractFinancialInfo: " + e.getMessage());
			return false;
		}
	}
}
