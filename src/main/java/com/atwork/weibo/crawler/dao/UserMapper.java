package com.atwork.weibo.crawler.dao;

import java.util.List;

import com.atwork.weibo.crawler.domain.User;

public interface UserMapper {
	
	void save(User user);
	void bulkSave(List<User> users);
	void update(User user);
//	void delete(int id);
	User findByUid(long uid);
	User findByFlag(boolean flag);
}
