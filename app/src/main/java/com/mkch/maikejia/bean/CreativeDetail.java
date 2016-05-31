package com.mkch.maikejia.bean;

import java.util.List;

public class CreativeDetail {
	private int id;
	private Channel channel;
	private String title;
	private String desc;
	private String txt;
	private String txt1;
	private int status;
	private int assessStatus;
	private int designStatus;
	private int agentStatus;
	private List<Pic> pics;
	private String releaseDate;
	private String remainTime;
	private CUser user;
	private int views;
	private int collections;
	private int ups;
	private int downs;
	private ContentAssess contentAssess;
	private UserStatus userStatus;
	
	public CreativeDetail() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
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

	public String getTxt() {
		return txt;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}

	public String getTxt1() {
		return txt1;
	}

	public void setTxt1(String txt1) {
		this.txt1 = txt1;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getAssessStatus() {
		return assessStatus;
	}

	public void setAssessStatus(int assessStatus) {
		this.assessStatus = assessStatus;
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

	public List<Pic> getPics() {
		return pics;
	}

	public void setPics(List<Pic> pics) {
		this.pics = pics;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getRemainTime() {
		return remainTime;
	}

	public void setRemainTime(String remainTime) {
		this.remainTime = remainTime;
	}

	public CUser getUser() {
		return user;
	}

	public void setUser(CUser user) {
		this.user = user;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public int getCollections() {
		return collections;
	}

	public void setCollections(int collections) {
		this.collections = collections;
	}

	public int getUps() {
		return ups;
	}

	public void setUps(int ups) {
		this.ups = ups;
	}

	public int getDowns() {
		return downs;
	}

	public void setDowns(int downs) {
		this.downs = downs;
	}

	public ContentAssess getContentAssess() {
		return contentAssess;
	}

	public void setContentAssess(ContentAssess contentAssess) {
		this.contentAssess = contentAssess;
	}

	public UserStatus getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	public CreativeDetail(int id, Channel channel, String title, String desc,
			String txt, String txt1, int status, int assessStatus,
			int designStatus, int agentStatus, List<Pic> pics,
			String releaseDate, String remainTime, CUser user, int views,
			int collections, int ups, int downs, ContentAssess contentAssess,
			UserStatus userStatus) {
		super();
		this.id = id;
		this.channel = channel;
		this.title = title;
		this.desc = desc;
		this.txt = txt;
		this.txt1 = txt1;
		this.status = status;
		this.assessStatus = assessStatus;
		this.designStatus = designStatus;
		this.agentStatus = agentStatus;
		this.pics = pics;
		this.releaseDate = releaseDate;
		this.remainTime = remainTime;
		this.user = user;
		this.views = views;
		this.collections = collections;
		this.ups = ups;
		this.downs = downs;
		this.contentAssess = contentAssess;
		this.userStatus = userStatus;
	}

	@Override
	public String toString() {
		return "CreativeDetail [id=" + id + ", channel=" + channel + ", title="
				+ title + ", desc=" + desc + ", txt=" + txt + ", txt1=" + txt1
				+ ", status=" + status + ", assessStatus=" + assessStatus
				+ ", designStatus=" + designStatus + ", agentStatus="
				+ agentStatus + ", pics=" + pics + ", releaseDate="
				+ releaseDate + ", remainTime=" + remainTime + ", user=" + user
				+ ", views=" + views + ", collections=" + collections
				+ ", ups=" + ups + ", downs=" + downs + ", contentAssess="
				+ contentAssess + ", userStatus=" + userStatus + "]";
	}
	
	
	
}
