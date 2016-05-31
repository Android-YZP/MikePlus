package com.mkch.maikejia.bean;

public class CUser {
	private int id;
	private String username;
	private String userImg;
	private int creatives;
	private int designs;
	private int fans;
	public CUser() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CUser(int id,String username, String userImg, int creatives, int designs,
			int fans) {
		super();
		this.id = id;
		this.username = username;
		this.userImg = userImg;
		this.creatives = creatives;
		this.designs = designs;
		this.fans = fans;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserImg() {
		return userImg;
	}
	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}
	public int getCreatives() {
		return creatives;
	}
	public void setCreatives(int creatives) {
		this.creatives = creatives;
	}
	public int getDesigns() {
		return designs;
	}
	public void setDesigns(int designs) {
		this.designs = designs;
	}
	public int getFans() {
		return fans;
	}
	public void setFans(int fans) {
		this.fans = fans;
	}
	@Override
	public String toString() {
		return "CUser [id=" + id + ", username=" + username + ", userImg="
				+ userImg + ", creatives=" + creatives + ", designs=" + designs
				+ ", fans=" + fans + "]";
	}
	
}
