package com.mkch.maikejia.bean;

public class Pic {
	private String imgPath;
	private String desc;
	public Pic() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Pic(String imgPath, String desc) {
		super();
		this.imgPath = imgPath;
		this.desc = desc;
	}
	@Override
	public String toString() {
		return "Pic [imgPath=" + imgPath + ", desc=" + desc + "]";
	}
	
}
