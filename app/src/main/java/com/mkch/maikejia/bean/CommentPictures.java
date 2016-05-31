package com.mkch.maikejia.bean;

public class CommentPictures {
	private String imgPath;

	public CommentPictures() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CommentPictures(String imgPath) {
		super();
		this.imgPath = imgPath;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	@Override
	public String toString() {
		return "CommentPictures [imgPath=" + imgPath + "]";
	}
	
}
