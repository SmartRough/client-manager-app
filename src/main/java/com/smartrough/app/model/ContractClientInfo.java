package com.smartrough.app.model;

public class ContractClientInfo {
	private Long id;
	private String companyName;
	private String ownerName1;
	private String ownerName2;
	private String email;
	private String address;
	private String homePhone;
	private String otherPhone;

	public ContractClientInfo() {
		super();
	}

	public ContractClientInfo(Long id, String companyName, String ownerName1, String ownerName2, String email,
			String address, String homePhone, String otherPhone) {
		super();
		this.id = id;
		this.companyName = companyName;
		this.ownerName1 = ownerName1;
		this.ownerName2 = ownerName2;
		this.email = email;
		this.address = address;
		this.homePhone = homePhone;
		this.otherPhone = otherPhone;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getOwnerName1() {
		return ownerName1;
	}

	public void setOwnerName1(String ownerName1) {
		this.ownerName1 = ownerName1;
	}

	public String getOwnerName2() {
		return ownerName2;
	}

	public void setOwnerName2(String ownerName2) {
		this.ownerName2 = ownerName2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getOtherPhone() {
		return otherPhone;
	}

	public void setOtherPhone(String otherPhone) {
		this.otherPhone = otherPhone;
	}
}
