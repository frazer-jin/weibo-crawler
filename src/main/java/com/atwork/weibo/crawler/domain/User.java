package com.atwork.weibo.crawler.domain;

import java.sql.Date;

public class User {

	private int id;
	private long uid;
	private String nickName;
	private char sex; // F OR M
	private boolean flag;
	private Date create;
	
	public User(long uid, String nickName, char sex) {
		super();
		this.uid = uid;
		this.nickName = nickName;
		this.sex = sex;
	}
	public User() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public char getSex() {
		return sex;
	}
	public void setSex(char sex) {
		this.sex = sex;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public Date getCreate() {
		return create;
	}
	public void setCreate(Date create) {
		this.create = create;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", uid=" + uid + ", nickName=" + nickName + ", sex=" + sex + ", flag=" + flag
				+ ", create=" + create + "]";
	}
	
}
