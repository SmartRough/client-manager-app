package com.smartrough.app.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Invoice {
	private Long id;
	private String invoiceNumber;
	private LocalDate date;
	private Long companyId; // own company
	private Long customerId; // client
	private BigDecimal subtotal;
	private BigDecimal taxRate;
	private BigDecimal additionalCosts;
	private BigDecimal total;
	private String notes;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private List<InvoiceItem> items;

	public Invoice() {
		super();
	}

	public Invoice(Long id, String invoiceNumber, LocalDate date, Long companyId, Long customerId, BigDecimal subtotal,
			BigDecimal taxRate, BigDecimal additionalCosts, BigDecimal total, String notes, LocalDateTime createdAt,
			LocalDateTime updatedAt, List<InvoiceItem> items) {
		super();
		this.id = id;
		this.invoiceNumber = invoiceNumber;
		this.date = date;
		this.companyId = companyId;
		this.customerId = customerId;
		this.subtotal = subtotal;
		this.taxRate = taxRate;
		this.additionalCosts = additionalCosts;
		this.total = total;
		this.notes = notes;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.items = items;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
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

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public BigDecimal getAdditionalCosts() {
		return additionalCosts;
	}

	public void setAdditionalCosts(BigDecimal additionalCosts) {
		this.additionalCosts = additionalCosts;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<InvoiceItem> getItems() {
		return items;
	}

	public void setItems(List<InvoiceItem> items) {
		this.items = items;
	}

}
