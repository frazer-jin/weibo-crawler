package com.atwork.weibo.crawler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.atwork.weibo.crawler.dao.UserMapper;
import com.atwork.weibo.crawler.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/application-root.xml")
public class DaoTest {
	
	@Autowired
	private UserMapper userMapper;

	@Test
	public void test1(){
		System.out.println(userMapper);
	}
	
	@Test
	public void testSelectByFlag(){
		boolean flag = false;
		User user = userMapper.findByFlag(flag);
		System.out.println(user);
	}
	@Test
	public void testSelectByUid(){
		User user = userMapper.findByUid(1005056032746934L);
		System.out.println(user);
	}
}
