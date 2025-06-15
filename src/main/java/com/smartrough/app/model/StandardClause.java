package com.smartrough.app.model;

public class StandardClause {
	private Long id;
	private Integer order;
	private String title;
	private String content;
	private boolean active;
	private Long templateId;

	public StandardClause() {
		super();
	}

	public StandardClause(Long id, Integer order, String title, String content, boolean active, Long templateId) {
		super();
		this.id = id;
		this.order = order;
		this.title = title;
		this.content = content;
		this.active = active;
		this.templateId = templateId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

}
