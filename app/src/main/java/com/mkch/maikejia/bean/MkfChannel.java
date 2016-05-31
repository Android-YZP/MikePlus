package com.mkch.maikejia.bean;

public class MkfChannel {
	private int id;
	private String name;
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
	public MkfChannel() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MkfChannel(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	@Override
	public String toString() {
		return "MkfChannel [id=" + id + ", name=" + name + "]";
	}
	
}
