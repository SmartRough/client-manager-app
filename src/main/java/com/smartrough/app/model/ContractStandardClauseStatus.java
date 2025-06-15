package com.smartrough.app.model;

public class ContractStandardClauseStatus {

	private Long contractId;
	private Long clauseId;
	private boolean initialed;

	public ContractStandardClauseStatus() {
		super();
	}

	public ContractStandardClauseStatus(Long contractId, Long clauseId, boolean initialed) {
		super();
		this.contractId = contractId;
		this.clauseId = clauseId;
		this.initialed = initialed;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public Long getClauseId() {
		return clauseId;
	}

	public void setClauseId(Long clauseId) {
		this.clauseId = clauseId;
	}

	public boolean isInitialed() {
		return initialed;
	}

	public void setInitialed(boolean initialed) {
		this.initialed = initialed;
	}

}
