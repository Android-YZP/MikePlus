package com.mkch.maikejia.bean;

import java.util.List;

public class Comment {
	private int id;
	private CommentUser commentUser;
	private String text;
	private List<CommentPictures> pictures;
	private String createTime;
	private int replyCount;
	private List<Reply> replys;
	public Comment() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Comment(int id, CommentUser commentUser, String text,
			List<CommentPictures> pictures, String createTime, int replyCount,List<Reply> replys) {
		super();
		this.id = id;
		this.commentUser = commentUser;
		this.text = text;
		this.pictures = pictures;
		this.createTime = createTime;
		this.replyCount = replyCount;
		this.replys = replys;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public CommentUser getCommentUser() {
		return commentUser;
	}
	public void setCommentUser(CommentUser commentUser) {
		this.commentUser = commentUser;
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
	public int getReplyCount() {
		return replyCount;
	}
	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}
	public List<Reply> getReplys() {
		return replys;
	}
	public void setReplys(List<Reply> replys) {
		this.replys = replys;
	}
	@Override
	public String toString() {
		return "Comment [id=" + id + ", commentUser=" + commentUser + ", text="
				+ text + ", pictures=" + pictures + ", createTime="
				+ createTime + ", replyCount=" + replyCount + "]";
	}
	
	
	
}
