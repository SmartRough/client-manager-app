package com.smartrough.app.model;

public class ContractAttachment {

	private Long id;
	private Long contractId;
	private String name; // e.g., "proposal"
	private String extension; // e.g., "pdf"

	public ContractAttachment() {
	}

	public ContractAttachment(Long id, Long contractId, String name, String extension) {
		this.id = id;
		this.contractId = contractId;
		this.name = name;
		this.extension = extension;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getFullFilename() {
		return name + "." + extension;
	}
}
