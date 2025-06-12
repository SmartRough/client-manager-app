package com.smartrough.app.model;

public class Company {
	private long id;
	private String name;
	private String representative;
	private String phone;
	private String email;
	private long addressId;
	private boolean isOwnCompany;

	public Company() {
	}

	public Company(long id, String name, String representative, String phone, String email, long addressId,
			boolean isOwnCompany) {
		this.id = id;
		this.name = name;
		this.representative = representative;
		this.phone = phone;
		this.email = email;
		this.addressId = addressId;
		this.isOwnCompany = isOwnCompany;
	}

	// Getters and Setters

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRepresentative() {
		return representative;
	}

	public void setRepresentative(String representative) {
		this.representative = representative;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getAddressId() {
		return addressId;
	}

	public void setAddressId(long addressId) {
		this.addressId = addressId;
	}

	public boolean isOwnCompany() {
		return isOwnCompany;
	}

	public void setOwnCompany(boolean ownCompany) {
		isOwnCompany = ownCompany;
	}
}
