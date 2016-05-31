package com.mkch.maikejia.bean;

public class UserComment {
	private int id;
	private String url;
	private String title;
	private String time;
	private String text;
	private String username;
	public UserComment() {
		super();
	}
	public UserComment(int id, String url, String title, String time,
			String text,String username) {
		super();
		this.id = id;
		this.url = url;
		this.title = title;
		this.time = time;
		this.text = text;
		this.username = username;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public String toString() {
		return "UserComment [id=" + id + ", url=" + url + ", title=" + title
				+ ", time=" + time + ", text=" + text+ ", username=" + username + "]";
	}
	
}
