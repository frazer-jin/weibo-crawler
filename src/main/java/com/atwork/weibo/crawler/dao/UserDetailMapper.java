package com.atwork.weibo.crawler.dao;

import java.util.List;

import com.atwork.weibo.crawler.domain.User;
import com.atwork.weibo.crawler.domain.UserDetail;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

public interface UserDetailMapper {

	List<UserDetail> findByFlag(boolean flag);
	Integer findByUid(long uid);
	void save(User user);
	void update(UserDetail userDetail);
	void updateRetry(UserDetail user);
	PageList<UserDetail> findByPage(PageBounds page);
}
