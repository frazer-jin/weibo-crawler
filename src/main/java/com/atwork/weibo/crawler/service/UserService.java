package com.atwork.weibo.crawler.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atwork.weibo.crawler.dao.UserDetailMapper;
import com.atwork.weibo.crawler.dao.UserMapper;
import com.atwork.weibo.crawler.domain.User;
import com.atwork.weibo.crawler.domain.UserDetail;

@Service
public class UserService {
	
	@Value("${common.retry.count}")
	private int retryCount;

	@Autowired
	private UserMapper userDao;
	@Autowired
	private UserDetailMapper userDetailDao;

	@Transactional(readOnly = true)
	public User getUnCrawledUser() {
		return userDao.findByFlag(false);
	}

	@Transactional(readOnly = true)
	public List<UserDetail> getUnCrawledUserDetail() {
		return userDetailDao.findByFlag(false);
	}

	@Transactional
	public void addCrawledFollowers(User user, List<User> followers) {
		user.setFlag(true);
		userDao.update(user);
		if (followers.size() > 0) {
			userDao.bulkSave(followers);
			for (User f : followers) {
				userDetailDao.save(f);
			}
		}
	}

	@Transactional
	public void updateUserDetail(UserDetail userDetail) {
		userDetailDao.update(userDetail);
	}

	@Transactional
	public void updateUserDetailRetry(UserDetail user){
		user.setRetried(user.getRetried()+1);
		if(user.getRetried()>= retryCount){
			user.setFlag(true);
		}
		userDetailDao.updateRetry(user);
	}
}
