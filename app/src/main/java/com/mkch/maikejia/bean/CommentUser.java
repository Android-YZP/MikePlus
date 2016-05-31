package com.mkch.maikejia.bean;

public class CommentUser {
	private String username;
	private String userImg;
	public CommentUser() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CommentUser(String username, String userImg) {
		super();
		this.username = username;
		this.userImg = userImg;
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
	@Override
	public String toString() {
		return "CommentUser [username=" + username + ", userImg=" + userImg
				+ "]";
	}
	
}
