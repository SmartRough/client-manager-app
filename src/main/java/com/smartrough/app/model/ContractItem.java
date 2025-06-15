package com.smartrough.app.model;

public class ContractItem {

	private Long id;
	private Long contractId;
	private Integer order;
	private String description;

	public ContractItem() {
		super();
	}

	public ContractItem(Long id, Long contractId, Integer order, String description) {
		super();
		this.id = id;
		this.contractId = contractId;
		this.order = order;
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

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
