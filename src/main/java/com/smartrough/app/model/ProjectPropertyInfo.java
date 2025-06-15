package com.smartrough.app.model;

public class ProjectPropertyInfo {
	private Long id;
	private Long contractId;
	private boolean isHome;
	private boolean isCondo;
	private boolean isMFH;
	private boolean isCommercial;
	private Boolean isHOA;

	public ProjectPropertyInfo() {
		super();
	}

	public ProjectPropertyInfo(Long id, Long contractId, boolean isHome, boolean isCondo, boolean isMFH,
			boolean isCommercial, Boolean isHOA) {
		super();
		this.id = id;
		this.contractId = contractId;
		this.isHome = isHome;
		this.isCondo = isCondo;
		this.isMFH = isMFH;
		this.isCommercial = isCommercial;
		this.isHOA = isHOA;
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

	public boolean isHome() {
		return isHome;
	}

	public void setHome(boolean isHome) {
		this.isHome = isHome;
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

	public Boolean getIsHOA() {
		return isHOA;
	}

	public void setIsHOA(Boolean isHOA) {
		this.isHOA = isHOA;
	}

}
