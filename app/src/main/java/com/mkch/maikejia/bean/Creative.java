package com.mkch.maikejia.bean;

/**
 * 创意
 * @author sunny
 *
 */
public class Creative {
	//创意列表
	private String id;
	private String title;
	private String url;
	private String titleImg;
	private String username;
	private String userImg;
	private String desc;
	private String releaseDate;
	//我的创意列表
	private int views;
	private int commentsCount;
	private int status;//0-草稿1-审核中2-审核通过3-已结束5-审核未通过6-已过期
	private int designStatus;//1设计中2 设计完成
	private int agentStatus;//1代理进行中2代理已经结束
	private int assessStatus;///1评估中2评估通过3评估不通过
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTitleImg() {
		return titleImg;
	}
	public void setTitleImg(String titleImg) {
		this.titleImg = titleImg;
	}
	public String getUserImg() {
		return userImg;
	}
	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public int getViews() {
		return views;
	}
	public void setViews(int views) {
		this.views = views;
	}
	public int getCommentsCount() {
		return commentsCount;
	}
	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getDesignStatus() {
		return designStatus;
	}
	public void setDesignStatus(int designStatus) {
		this.designStatus = designStatus;
	}
	public int getAgentStatus() {
		return agentStatus;
	}
	public void setAgentStatus(int agentStatus) {
		this.agentStatus = agentStatus;
	}
	public int getAssessStatus() {
		return assessStatus;
	}
	public void setAssessStatus(int assessStatus) {
		this.assessStatus = assessStatus;
	}
	@Override
	public String toString() {
		return "Creative [id=" + id + ", title=" + title + ", url=" + url
				+ ", titleImg=" + titleImg + ", username=" + username
				+ ", userImg=" + userImg + ", desc=" + desc + ", releaseDate="
				+ releaseDate + ", views=" + views + ", commentsCount="
				+ commentsCount + ", status=" + status + ", designStatus="
				+ designStatus + ", agentStatus=" + agentStatus
				+ ", assessStatus=" + assessStatus + "]";
	}
	
	
	
}
