package com.mkch.maikejia.bean;

public class ContentAssess {
	private boolean draft;
	private String text;
	private String createTime;
	private String username;
	public ContentAssess() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ContentAssess(boolean draft, String text, String createTime,
			String username) {
		super();
		this.draft = draft;
		this.text = text;
		this.createTime = createTime;
		this.username = username;
	}
	public boolean isDraft() {
		return draft;
	}
	public void setDraft(boolean draft) {
		this.draft = draft;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public String toString() {
		return "ContentAssess [draft=" + draft + ", text=" + text
				+ ", createTime=" + createTime + ", username=" + username + "]";
	}
	
}
