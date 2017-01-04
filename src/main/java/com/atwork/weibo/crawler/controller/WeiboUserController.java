package com.atwork.weibo.crawler.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.atwork.weibo.crawler.domain.UserDetail;
import com.atwork.weibo.crawler.service.UserService;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;

@Controller
@RequestMapping(value = "/users")
public class WeiboUserController {
	
	private static Logger logger = LoggerFactory.getLogger(WeiboUserController.class);
	
	@Autowired
	private UserService userService;

	@RequestMapping("")
	public String getUsers(@RequestParam(required = false, defaultValue = "1") int pageNum,
							@RequestParam(required = false, defaultValue = "10") int pageSize,
							HttpServletRequest request) {

		PageBounds page = new PageBounds(pageNum, pageSize);
		
		PageList<UserDetail> users = userService.findUserDetailsByPage(page);
		
		request.setAttribute("users", users);
		request.setAttribute("total", users.getPaginator().getTotalCount());
		request.setAttribute("currentPage", pageNum);
		
		logger.debug("get user list {}, page {}", users, page);
		
		return "users";
	}
}
