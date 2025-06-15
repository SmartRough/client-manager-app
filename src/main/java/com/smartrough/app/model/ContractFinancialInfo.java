package com.smartrough.app.model;

import java.math.BigDecimal;

public class ContractFinancialInfo {
	private Long id;
	private Long contractId;
	private BigDecimal totalPrice;
	private BigDecimal deposit;
	private BigDecimal balanceDueUponCompletion;
	private BigDecimal amountFinanced;

	public ContractFinancialInfo() {
		super();
	}

	public ContractFinancialInfo(Long id, Long contractId, BigDecimal totalPrice, BigDecimal deposit,
			BigDecimal balanceDueUponCompletion, BigDecimal amountFinanced) {
		super();
		this.id = id;
		this.contractId = contractId;
		this.totalPrice = totalPrice;
		this.deposit = deposit;
		this.balanceDueUponCompletion = balanceDueUponCompletion;
		this.amountFinanced = amountFinanced;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getDeposit() {
		return deposit;
	}

	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}

	public BigDecimal getBalanceDueUponCompletion() {
		return balanceDueUponCompletion;
	}

	public void setBalanceDueUponCompletion(BigDecimal balanceDueUponCompletion) {
		this.balanceDueUponCompletion = balanceDueUponCompletion;
	}

	public BigDecimal getAmountFinanced() {
		return amountFinanced;
	}

	public void setAmountFinanced(BigDecimal amountFinanced) {
		this.amountFinanced = amountFinanced;
	}

}
