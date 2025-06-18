package com.smartrough.app.dao;

import com.smartrough.app.model.Contract;
import com.smartrough.app.model.ContractAttachment;
import com.smartrough.app.model.ContractItem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContractDAO {

	public static long save(Contract contract) {
		String[] columns = { "po_number", "measure_date", "startDate", "endDate", "owner1", "owner2", "address", "city",
				"state", "zip", "email", "home_phone", "other_phone", "is_house", "is_condo", "is_mfh", "is_commercial",
				"has_hoa", "total_price", "deposit", "balance_due", "amount_financed", "card_type", "card_number",
				"card_zip", "card_cvc", "card_exp" };

		Object[] values = { contract.getPoNumber(),
				contract.getMeasureDate() != null ? contract.getMeasureDate().toString() : null,
				contract.getStartDate() != null ? contract.getStartDate().toString() : null,
				contract.getEndDate() != null ? contract.getEndDate().toString() : null, contract.getOwner1(),
				contract.getOwner2(), contract.getAddress(), contract.getCity(), contract.getState(), contract.getZip(),
				contract.getEmail(), contract.getHomePhone(), contract.getOtherPhone(), contract.isHouse(),
				contract.isCondo(), contract.isMFH(), contract.isCommercial(), contract.isHasHOA(),
				contract.getTotalPrice(), contract.getDeposit(), contract.getBalanceDue(), contract.getAmountFinanced(),
				contract.getCardType(), contract.getCardNumber(), contract.getCardZip(), contract.getCardCVC(),
				contract.getCardExp() };

		int[] types = { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN, Types.DOUBLE, Types.DOUBLE,
				Types.DOUBLE, Types.DOUBLE, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR };

		long contractId = CRUDHelper.create("Contract", columns, values, types);

		if (contract.getItems() != null) {
			for (ContractItem item : contract.getItems()) {
				item.setContractId(contractId);
				ContractItemDAO.save(item);
			}
		}

		if (contract.getAttachments() != null) {
			for (ContractAttachment attachment : contract.getAttachments()) {
				attachment.setContractId(contractId);
				ContractAttachmentDAO.save(attachment);
			}
		}

		return contractId;
	}

	public static boolean update(Contract contract) {
		String sql = """
					UPDATE Contract SET po_number=?, measure_date=?, startDate=?, endDate=?, owner1=?, owner2=?, address=?, city=?, state=?, zip=?,
					email=?, home_phone=?, other_phone=?, is_house=?, is_condo=?, is_mfh=?, is_commercial=?, has_hoa=?,
					total_price=?, deposit=?, balance_due=?, amount_financed=?, card_type=?, card_number=?, card_zip=?,
					card_cvc=?, card_exp=? WHERE id=?
				""";

		try (Connection conn = Database.connect()) {
			conn.setAutoCommit(false);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, contract.getPoNumber());
				stmt.setString(2, contract.getMeasureDate() != null ? contract.getMeasureDate().toString() : null);
				stmt.setString(3, contract.getStartDate() != null ? contract.getStartDate().toString() : null);
				stmt.setString(4, contract.getEndDate() != null ? contract.getEndDate().toString() : null);
				stmt.setString(5, contract.getOwner1());
				stmt.setString(6, contract.getOwner2());
				stmt.setString(7, contract.getAddress());
				stmt.setString(8, contract.getCity());
				stmt.setString(9, contract.getState());
				stmt.setString(10, contract.getZip());
				stmt.setString(11, contract.getEmail());
				stmt.setString(12, contract.getHomePhone());
				stmt.setString(13, contract.getOtherPhone());
				stmt.setBoolean(14, contract.isHouse());
				stmt.setBoolean(15, contract.isCondo());
				stmt.setBoolean(16, contract.isMFH());
				stmt.setBoolean(17, contract.isCommercial());
				stmt.setBoolean(18, contract.isHasHOA());
				stmt.setDouble(19, contract.getTotalPrice() != null ? contract.getTotalPrice() : 0.0);
				stmt.setDouble(20, contract.getDeposit() != null ? contract.getDeposit() : 0.0);
				stmt.setDouble(21, contract.getBalanceDue() != null ? contract.getBalanceDue() : 0.0);
				stmt.setDouble(22, contract.getAmountFinanced() != null ? contract.getAmountFinanced() : 0.0);
				stmt.setString(23, contract.getCardType());
				stmt.setString(24, contract.getCardNumber());
				stmt.setString(25, contract.getCardZip());
				stmt.setString(26, contract.getCardCVC());
				stmt.setString(27, contract.getCardExp());
				stmt.setLong(28, contract.getId());

				if (stmt.executeUpdate() == 0) {
					conn.rollback();
					System.err.println(">> ERROR: No se pudo actualizar el contrato.");
					return false;
				}

				long contractId = contract.getId();

				if (!ContractItemDAO.deleteByContractId(conn, contractId)) {
					conn.rollback();
					System.err.println(">> ERROR al eliminar ítems existentes.");
					return false;
				}

				if (!ContractAttachmentDAO.deleteByContractId(conn, contractId)) {
					conn.rollback();
					System.err.println(">> ERROR al eliminar adjuntos existentes.");
					return false;
				}

				if (contract.getItems() != null) {
					for (ContractItem item : contract.getItems()) {
						item.setContractId(contractId);
						if (ContractItemDAO.save(conn, item) == -1) {
							conn.rollback();
							System.err.println(">> ERROR al insertar ítem.");
							return false;
						}
					}
				}

				if (contract.getAttachments() != null) {
					for (ContractAttachment attachment : contract.getAttachments()) {
						attachment.setContractId(contractId);
						if (ContractAttachmentDAO.save(conn, attachment) == -1) {
							conn.rollback();
							System.err.println(">> ERROR al insertar adjunto.");
							return false;
						}
					}
				}

				conn.commit();
				return true;

			} catch (Exception e) {
				conn.rollback();
				e.printStackTrace();
				return false;
			} finally {
				conn.setAutoCommit(true);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static Contract findById(long id) {
		String sql = "SELECT * FROM Contract WHERE id = ?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				Contract contract = mapResultSet(rs);
				contract.setAttachments(ContractAttachmentDAO.findByContractId(id));
				contract.setItems(ContractItemDAO.findByContractId(id));
				return contract;
			}
		} catch (SQLException e) {
			System.err.println("Error fetching contract: " + e.getMessage());
		}
		return null;
	}

	public static List<Contract> findAll() {
		List<Contract> list = new ArrayList<>();
		String sql = "SELECT * FROM Contract ORDER BY measure_date DESC";
		try (Connection conn = Database.connect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				Contract contract = mapResultSet(rs);
				long id = contract.getId();
				contract.setAttachments(ContractAttachmentDAO.findByContractId(id));
				contract.setItems(ContractItemDAO.findByContractId(id));
				list.add(contract);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching contracts: " + e.getMessage());
		}
		return list;
	}

	public static boolean delete(long id) {
		String sql = "DELETE FROM Contract WHERE id=?";
		try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error deleting contract: " + e.getMessage());
			return false;
		}
	}

	private static Contract mapResultSet(ResultSet rs) throws SQLException {
		Contract c = new Contract();
		c.setId(rs.getLong("id"));
		c.setPoNumber(rs.getString("po_number"));

		try {
			String dateStr = rs.getString("measure_date");
			if (dateStr != null && !dateStr.isBlank()) {
				c.setMeasureDate(LocalDate.parse(dateStr)); // yyyy-MM-dd
			}
		} catch (Exception ex) {
			System.err.println(">> ERROR parsing measure date: " + ex.getMessage());
			ex.printStackTrace();
			c.setMeasureDate(null);
		}

		try {
			String dateStr = rs.getString("startDate");
			if (dateStr != null && !dateStr.isBlank()) {
				c.setStartDate(LocalDate.parse(dateStr)); // yyyy-MM-dd
			}
		} catch (Exception ex) {
			System.err.println(">> ERROR parsing start date: " + ex.getMessage());
			ex.printStackTrace();
			c.setStartDate(null);
		}

		try {
			String dateStr = rs.getString("endDate");
			if (dateStr != null && !dateStr.isBlank()) {
				c.setEndDate(LocalDate.parse(dateStr)); // yyyy-MM-dd
			}
		} catch (Exception ex) {
			System.err.println(">> ERROR parsing end date: " + ex.getMessage());
			ex.printStackTrace();
			c.setEndDate(null);
		}
		
		c.setOwner1(rs.getString("owner1"));
		c.setOwner2(rs.getString("owner2"));
		c.setAddress(rs.getString("address"));
		c.setCity(rs.getString("city"));
		c.setState(rs.getString("state"));
		c.setZip(rs.getString("zip"));
		c.setEmail(rs.getString("email"));
		c.setHomePhone(rs.getString("home_phone"));
		c.setOtherPhone(rs.getString("other_phone"));
		c.setHouse(rs.getBoolean("is_house"));
		c.setCondo(rs.getBoolean("is_condo"));
		c.setMFH(rs.getBoolean("is_mfh"));
		c.setCommercial(rs.getBoolean("is_commercial"));
		c.setHasHOA(rs.getBoolean("has_hoa"));
		c.setTotalPrice(rs.getDouble("total_price"));
		c.setDeposit(rs.getDouble("deposit"));
		c.setBalanceDue(rs.getDouble("balance_due"));
		c.setAmountFinanced(rs.getDouble("amount_financed"));
		c.setCardType(rs.getString("card_type"));
		c.setCardNumber(rs.getString("card_number"));
		c.setCardZip(rs.getString("card_zip"));
		c.setCardCVC(rs.getString("card_cvc"));
		c.setCardExp(rs.getString("card_exp"));
		c.setAttachments(new ArrayList<>());
		c.setItems(new ArrayList<>());
		return c;
	}
}