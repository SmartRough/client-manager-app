package com.smartrough.app.model;

import java.time.LocalDate;
import java.util.List;

public class Contract {

	private Long id;

	// Basic info
	private String poNumber;
	private LocalDate measureDate;
	private LocalDate startDate;
	private LocalDate endDate;

	// Owners and contact
	private String owner1;
	private String owner2;
	private String address;
	private String city;
	private String state;
	private String zip;
	private String email;
	private String homePhone;
	private String otherPhone;

	// Property type
	private boolean isHouse;
	private boolean isCondo;
	private boolean isMFH;
	private boolean isCommercial;
	private boolean hasHOA;

	// Financials
	private Double totalPrice;
	private Double deposit;
	private Double balanceDue;
	private Double amountFinanced;

	// Card info
	private String cardType;
	private String cardNumber;
	private String cardZip;
	private String cardCVC;
	private String cardExp;

	// Attached files (store filenames)
	private List<ContractAttachment> attachments;

	private List<ContractItem> items;

	public Contract() {
		super();
	}

	public Contract(Long id, String poNumber, LocalDate measureDate, LocalDate startDate, LocalDate endDate,
			String owner1, String owner2, String address, String city, String state, String zip, String email,
			String homePhone, String otherPhone, boolean isHouse, boolean isCondo, boolean isMFH, boolean isCommercial,
			boolean hasHOA, Double totalPrice, Double deposit, Double balanceDue, Double amountFinanced,
			String cardType, String cardNumber, String cardZip, String cardCVC, String cardExp,
			List<ContractAttachment> attachments, List<ContractItem> items) {
		super();
		this.id = id;
		this.poNumber = poNumber;
		this.measureDate = measureDate;
		this.startDate = startDate;
		this.endDate = endDate;
		this.owner1 = owner1;
		this.owner2 = owner2;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.email = email;
		this.homePhone = homePhone;
		this.otherPhone = otherPhone;
		this.isHouse = isHouse;
		this.isCondo = isCondo;
		this.isMFH = isMFH;
		this.isCommercial = isCommercial;
		this.hasHOA = hasHOA;
		this.totalPrice = totalPrice;
		this.deposit = deposit;
		this.balanceDue = balanceDue;
		this.amountFinanced = amountFinanced;
		this.cardType = cardType;
		this.cardNumber = cardNumber;
		this.cardZip = cardZip;
		this.cardCVC = cardCVC;
		this.cardExp = cardExp;
		this.attachments = attachments;
		this.items = items;
	}

	// Getters and Setters (puedes generar con Lombok si prefieres)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public LocalDate getMeasureDate() {
		return measureDate;
	}

	public void setMeasureDate(LocalDate measureDate) {
		this.measureDate = measureDate;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getOwner1() {
		return owner1;
	}

	public void setOwner1(String owner1) {
		this.owner1 = owner1;
	}

	public String getOwner2() {
		return owner2;
	}

	public void setOwner2(String owner2) {
		this.owner2 = owner2;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public boolean isHouse() {
		return isHouse;
	}

	public void setHouse(boolean isHouse) {
		this.isHouse = isHouse;
	}

	public boolean isCondo() {
		return isCondo;
	}

	public void setCondo(boolean isCondo) {
		this.isCondo = isCondo;
	}

	public boolean isMFH() {
		return isMFH;
	}

	public void setMFH(boolean isMFH) {
		this.isMFH = isMFH;
	}

	public boolean isCommercial() {
		return isCommercial;
	}

	public void setCommercial(boolean isCommercial) {
		this.isCommercial = isCommercial;
	}

	public boolean isHasHOA() {
		return hasHOA;
	}

	public void setHasHOA(boolean hasHOA) {
		this.hasHOA = hasHOA;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Double getDeposit() {
		return deposit;
	}

	public void setDeposit(Double deposit) {
		this.deposit = deposit;
	}

	public Double getBalanceDue() {
		return balanceDue;
	}

	public void setBalanceDue(Double balanceDue) {
		this.balanceDue = balanceDue;
	}

	public Double getAmountFinanced() {
		return amountFinanced;
	}

	public void setAmountFinanced(Double amountFinanced) {
		this.amountFinanced = amountFinanced;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardZip() {
		return cardZip;
	}

	public void setCardZip(String cardZip) {
		this.cardZip = cardZip;
	}

	public String getCardCVC() {
		return cardCVC;
	}

	public void setCardCVC(String cardCVC) {
		this.cardCVC = cardCVC;
	}

	public String getCardExp() {
		return cardExp;
	}

	public void setCardExp(String cardExp) {
		this.cardExp = cardExp;
	}

	public List<ContractAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<ContractAttachment> attachments) {
		this.attachments = attachments;
	}

	public List<ContractItem> getItems() {
		return items;
	}

	public void setItems(List<ContractItem> items) {
		this.items = items;
	}
}
