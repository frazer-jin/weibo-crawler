package com.atwork.weibo.crawler;

public interface EndPoint {

	String LOGIN_ENDPOINT = "https://passport.weibo.cn/signin/login?entry=mweibo&res=wel&wm=3349&r=http%3A%2F%2Fpad.weibo.cn%2F";
	String FOLLOW_ENDPOINT = "http://weibo.com/p/%s/follow";
	String DETAIL_ENDPOINT = "http://weibo.com/p/%s/info?mod=pedit_more";
}
