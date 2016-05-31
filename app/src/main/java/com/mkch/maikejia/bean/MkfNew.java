package com.mkch.maikejia.bean;

public class MkfNew {
	private int id;
	private MkfChannel channel;
	private String titleImg;
	private String title;
	private String desc;
	private String keywords;
	private String publishTime;
	private String content;
	private int views;
	public MkfNew() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "MkfNew [id=" + id + ", channel=" + channel + ", titleImg="
				+ titleImg + ", title=" + title + ", desc=" + desc
				+ ", keywords=" + keywords + ", publishTime=" + publishTime
				+ ", content=" + content+ ", views=" + views + "]";
	}
	public MkfNew(int id, MkfChannel channel, String titleImg, String title,
			String desc, String keywords, String publishTime,String content, int views) {
		super();
		this.id = id;
		this.channel = channel;
		this.titleImg = titleImg;
		this.title = title;
		this.desc = desc;
		this.keywords = keywords;
		this.publishTime = publishTime;
		this.content = content;
		this.views = views;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public MkfChannel getChannel() {
		return channel;
	}
	public void setChannel(MkfChannel channel) {
		this.channel = channel;
	}
	public String getTitleImg() {
		return titleImg;
	}
	public void setTitleImg(String titleImg) {
		this.titleImg = titleImg;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	public int getViews() {
		return views;
	}
	public void setViews(int views) {
		this.views = views;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	 
}
