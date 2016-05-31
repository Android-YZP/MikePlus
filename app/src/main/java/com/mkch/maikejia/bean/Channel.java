package com.mkch.maikejia.bean;

public class Channel {
	private int id;
	private String name;
	private String color;
	public Channel() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Channel(int id, String name, String color) {
		super();
		this.id = id;
		this.name = name;
		this.color = color;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	@Override
	public String toString() {
		return "Channel [id=" + id + ", name=" + name + ", color=" + color
				+ "]";
	}
	
}
