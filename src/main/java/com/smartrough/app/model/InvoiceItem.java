package com.smartrough.app.model;

import java.math.BigDecimal;

public class InvoiceItem {
	private Long id;
	private Long invoiceId;
	private String description;
	private BigDecimal amount;
	private Integer order;

	public InvoiceItem() {
		super();
	}

	public InvoiceItem(Long id, Long invoiceId, String description, BigDecimal amount, Integer order) {
		super();
		this.id = id;
		this.invoiceId = invoiceId;
		this.description = description;
		this.amount = amount;
		this.order = order;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
}
