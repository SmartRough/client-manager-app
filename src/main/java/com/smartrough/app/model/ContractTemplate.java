package com.smartrough.app.model;

import java.util.List;

public class ContractTemplate {
	private Long id;
	private String name;
	private String legalNotice; // Buyer's Right to Cancel

	private Company contractor; // Empresa que emite el contrato
	private List<StandardClause> clauses;

	public ContractTemplate() {
		super();
	}

	public ContractTemplate(Long id, String name, String legalNotice, Company contractor,
			List<StandardClause> clauses) {
		super();
		this.id = id;
		this.name = name;
		this.legalNotice = legalNotice;
		this.contractor = contractor;
		this.clauses = clauses;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLegalNotice() {
		return legalNotice;
	}

	public void setLegalNotice(String legalNotice) {
		this.legalNotice = legalNotice;
	}

	public Company getContractor() {
		return contractor;
	}

	public void setContractor(Company contractor) {
		this.contractor = contractor;
	}

	public List<StandardClause> getClauses() {
		return clauses;
	}

	public void setClauses(List<StandardClause> clauses) {
		this.clauses = clauses;
	}

}
