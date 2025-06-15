package com.smartrough.app.model;

import java.time.LocalDate;

public class Signature {
	private Long id;
	private Long contractId;
	private String name;
	private String role; // Ej: OWNER 1, OWNER 2, CONTRACTOR
	private LocalDate dateSigned;
	private String imageName; // Archivo de firma

	public Signature() {
		super();
	}

	public Signature(Long id, Long contractId, String name, String role, LocalDate dateSigned, String imageName) {
		super();
		this.id = id;
		this.contractId = contractId;
		this.name = name;
		this.role = role;
		this.dateSigned = dateSigned;
		this.imageName = imageName;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public LocalDate getDateSigned() {
		return dateSigned;
	}

	public void setDateSigned(LocalDate dateSigned) {
		this.dateSigned = dateSigned;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

}
