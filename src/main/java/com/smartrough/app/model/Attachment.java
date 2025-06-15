package com.smartrough.app.model;

public class Attachment {
	private Long id;
	private Long contractId;
	private String fileName;
	private String fileType;

	public Attachment() {
		super();
	}

	public Attachment(Long id, Long contractId, String fileName, String fileType) {
		super();
		this.id = id;
		this.contractId = contractId;
		this.fileName = fileName;
		this.fileType = fileType;
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}
