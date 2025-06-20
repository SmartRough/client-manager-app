package com.smartrough.app.model;

public class ContractItem {

	private Long id;
	private Long contractId;
	private String description;

	public ContractItem() {
		super();
	}

	public ContractItem(Long id, Long contractId, String description) {
		super();
		this.id = id;
		this.contractId = contractId;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
