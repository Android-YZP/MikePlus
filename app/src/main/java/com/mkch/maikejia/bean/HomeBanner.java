package com.mkch.maikejia.bean;

public class HomeBanner {
	private String img;
	private String desc;
	private int redirectType;
	private int redirectId;
	public HomeBanner() {
	}
	public HomeBanner(String img, String desc, int redirectType, int redirectId) {
		super();
		this.img = img;
		this.desc = desc;
		this.redirectType = redirectType;
		this.redirectId = redirectId;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getRedirectType() {
		return redirectType;
	}
	public void setRedirectType(int redirectType) {
		this.redirectType = redirectType;
	}
	public int getRedirectId() {
		return redirectId;
	}
	public void setRedirectId(int redirectId) {
		this.redirectId = redirectId;
	}
	
	
}
