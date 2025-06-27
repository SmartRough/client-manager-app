package com.smartrough.app.model;

public class EstimateItem {
	private Long id;
	private Long estimateId;
	private String description;
	private Integer order;

	public EstimateItem() {
	}

	public EstimateItem(Long id, Long estimateId, String description, Integer order) {
		this.id = id;
		this.estimateId = estimateId;
		this.description = description;
		this.order = order;
	}

	// Getters y Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEstimateId() {
		return estimateId;
	}

	public void setEstimateId(Long estimateId) {
		this.estimateId = estimateId;
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
