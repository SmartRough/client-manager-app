package com.smartrough.app.model;

public class ContractItem {

	private Long id;
	private Long contractId;
	private String description;
	private Integer order;

	public ContractItem() {
		super();
	}

	public ContractItem(Long id, Long contractId, String description, Integer order) {
		super();
		this.id = id;
		this.contractId = contractId;
		this.description = description;
		this.order = order;
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

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
}
