package com.smartrough.app.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Estimate {
	private Long id;
	private LocalDate date;
	private Long companyId;
	private Long customerId;
	private String approved_by;
	private String jobDescription;
	private BigDecimal total;
	private List<EstimateItem> items;
	private List<String> imageNames; // nombres de archivos (no el path completo)

	public Estimate() {
	}

	public Estimate(Long id, LocalDate date, Long companyId, Long customerId, String approved_by, String jobDescription,
			BigDecimal total, List<EstimateItem> items, List<String> imageNames) {
		this.id = id;
		this.date = date;
		this.companyId = companyId;
		this.customerId = customerId;
		this.approved_by = approved_by;
		this.jobDescription = jobDescription;
		this.total = total;
		this.items = items;
		this.imageNames = imageNames;
	}

	// Getters y Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getApproved_by() {
		return approved_by;
	}

	public void setApproved_by(String approved_by) {
		this.approved_by = approved_by;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public List<EstimateItem> getItems() {
		return items;
	}

	public void setItems(List<EstimateItem> items) {
		this.items = items;
	}

	public List<String> getImageNames() {
		return imageNames;
	}

	public void setImageNames(List<String> imageNames) {
		this.imageNames = imageNames;
	}
}
