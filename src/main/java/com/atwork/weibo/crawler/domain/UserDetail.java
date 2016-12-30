package com.atwork.weibo.crawler.domain;

import java.sql.Date;

public class UserDetail {

	private int id;
	private long uid;
	private String nickName;
	private char sex; // F OR M
	private Date birth;
	private Date create;
	private String address;
	private String college;
	private String memo;
	private int retried;
	private boolean flag;
	
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
	public int getRetried() {
		return retried;
	}
	public void setRetried(int retried) {
		this.retried = retried;
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
	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	public Date getCreate() {
		return create;
	}
	public void setCreate(Date create) {
		this.create = create;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getCollege() {
		return college;
	}
	public void setCollege(String college) {
		this.college = college;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	@Override
	public String toString() {
		return "UserDetail [id=" + id + ", uid=" + uid + ", nickName=" + nickName + ", sex=" + sex + ", birth=" + birth
				+ ", create=" + create + ", address=" + address + ", college=" + college + ", memo=" + memo
				+ ", retried=" + retried + ", flag=" + flag + "]";
	}
	
}
