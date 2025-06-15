package com.smartrough.app.model;

import java.time.LocalDate;
import java.util.List;

import com.smartrough.app.enums.ContractStatus;

public class Contract {
	private Long id;
	private String contractNumber;
	private LocalDate contractDate;

	private ContractTemplate template; // Plantilla base con cláusulas e info legal
	private ContractClientInfo clientInfo;
	private List<ContractItem> descriptionItems;
	private ProjectPropertyInfo propertyInfo;
	private ContractFinancialInfo financialInfo;
	private List<ContractStandardClauseStatus> clauseStatuses;
	private List<Attachment> attachments;
	private List<Signature> signatures;

	public Contract() {
		super();
	}

	private ContractStatus status;

	public Contract(Long id, String contractNumber, LocalDate contractDate, ContractTemplate template,
			ContractClientInfo clientInfo, List<ContractItem> descriptionItems, ProjectPropertyInfo propertyInfo,
			ContractFinancialInfo financialInfo, List<ContractStandardClauseStatus> clauseStatuses,
			List<Attachment> attachments, List<Signature> signatures, ContractStatus status) {
		super();
		this.id = id;
		this.contractNumber = contractNumber;
		this.contractDate = contractDate;
		this.template = template;
		this.clientInfo = clientInfo;
		this.descriptionItems = descriptionItems;
		this.propertyInfo = propertyInfo;
		this.financialInfo = financialInfo;
		this.clauseStatuses = clauseStatuses;
		this.attachments = attachments;
		this.signatures = signatures;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public LocalDate getContractDate() {
		return contractDate;
	}

	public void setContractDate(LocalDate contractDate) {
		this.contractDate = contractDate;
	}

	public ContractTemplate getTemplate() {
		return template;
	}

	public void setTemplate(ContractTemplate template) {
		this.template = template;
	}

	public ContractClientInfo getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(ContractClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}

	public List<ContractItem> getDescriptionItems() {
		return descriptionItems;
	}

	public void setDescriptionItems(List<ContractItem> descriptionItems) {
		this.descriptionItems = descriptionItems;
	}

	public ProjectPropertyInfo getPropertyInfo() {
		return propertyInfo;
	}

	public void setPropertyInfo(ProjectPropertyInfo propertyInfo) {
		this.propertyInfo = propertyInfo;
	}

	public ContractFinancialInfo getFinancialInfo() {
		return financialInfo;
	}

	public void setFinancialInfo(ContractFinancialInfo financialInfo) {
		this.financialInfo = financialInfo;
	}

	public List<ContractStandardClauseStatus> getClauseStatuses() {
		return clauseStatuses;
	}

	public void setClauseStatuses(List<ContractStandardClauseStatus> clauseStatuses) {
		this.clauseStatuses = clauseStatuses;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public List<Signature> getSignatures() {
		return signatures;
	}

	public void setSignatures(List<Signature> signatures) {
		this.signatures = signatures;
	}

	public ContractStatus getStatus() {
		return status;
	}

	public void setStatus(ContractStatus status) {
		this.status = status;
	}

}
