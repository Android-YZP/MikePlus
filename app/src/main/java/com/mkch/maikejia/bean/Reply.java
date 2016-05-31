package com.mkch.maikejia.bean;

import java.util.List;

public class Reply {
	private int id;
	private int type;//1：评论创意；2：回复评论
	private CommentUser user;
	private CommentUser replyUser;
	private String text;
	private List<CommentPictures> pictures;
	private String createTime;
	public Reply() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Reply(int id, int type, CommentUser user, CommentUser replyUser,
			String text, List<CommentPictures> pictures, String createTime) {
		super();
		this.id = id;
		this.type = type;
		this.user = user;
		this.replyUser = replyUser;
		this.text = text;
		this.pictures = pictures;
		this.createTime = createTime;
	}

	public CommentUser getUser() {
		return user;
	}
	public void setUser(CommentUser user) {
		this.user = user;
	}
	public CommentUser getReplyUser() {
		return replyUser;
	}
	public void setReplyUser(CommentUser replyUser) {
		this.replyUser = replyUser;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<CommentPictures> getPictures() {
		return pictures;
	}
	public void setPictures(List<CommentPictures> pictures) {
		this.pictures = pictures;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Reply [id=" + id + ", type=" + type + ", user=" + user
				+ ", replyUser=" + replyUser + ", text=" + text + ", pictures="
				+ pictures + ", createTime=" + createTime + "]";
	}
	
}
