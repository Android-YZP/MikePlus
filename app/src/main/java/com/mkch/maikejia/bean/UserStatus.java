package com.mkch.maikejia.bean;

public class UserStatus {
	private boolean collect;
	private boolean up;
	private boolean down;
	private boolean attention;
	public UserStatus() {
		super();
		// TODO Auto-generated constructor stub
	}
	public UserStatus(boolean collect, boolean up, boolean down,
			boolean attention) {
		super();
		this.collect = collect;
		this.up = up;
		this.down = down;
		this.attention = attention;
	}
	public boolean isCollect() {
		return collect;
	}
	public void setCollect(boolean collect) {
		this.collect = collect;
	}
	public boolean isUp() {
		return up;
	}
	public void setUp(boolean up) {
		this.up = up;
	}
	public boolean isDown() {
		return down;
	}
	public void setDown(boolean down) {
		this.down = down;
	}
	public boolean isAttention() {
		return attention;
	}
	public void setAttention(boolean attention) {
		this.attention = attention;
	}
	@Override
	public String toString() {
		return "UserStatus [collect=" + collect + ", up=" + up + ", down="
				+ down + ", attention=" + attention + "]";
	}
	
}
