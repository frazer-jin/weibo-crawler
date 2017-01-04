package com.atwork.weibo.crawler.executor;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.atwork.weibo.crawler.domain.User;
import com.atwork.weibo.crawler.service.UserService;
import com.atwork.weibo.crawler.util.Utils;
import com.gargoylesoftware.htmlunit.WebClient;

@Component
public class WeiboUserCrawlExecutor implements InitializingBean, DisposableBean {

	private static Logger logger = LoggerFactory.getLogger(WeiboUserCrawlExecutor.class);
	
	@Value("${common.weibo.username}")
	private String username;
	@Value("${common.weibo.password}")
	private String password;
	@Value("${common.crawler.enable}")
	private boolean enabled;

	private ScheduledExecutorService scheduler = null;
	
	@Autowired
	private UserService userService;

	@Override
	public void afterPropertiesSet() throws Exception {
		if(enabled){
			logger.info("start WeiboUserCrawlService scheduler... username {}, password {}", username, password);
			startScheduleExecutor();
		}
	}

	@Override
	public void destroy() throws Exception {
		logger.info("shutting down scheduler...");
		if(null != scheduler && !scheduler.isShutdown()){
			scheduler.shutdown();
		}
	}
	
	private void startScheduleExecutor() {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(new Task(), 10, 30, TimeUnit.SECONDS);
	}
	
	private void crawlUserFollowers(){
		try {
			WebClient client = Utils.getCrawlerClient(username, password);
			User user = userService.getUnCrawledUser();
			if(null == user){
				logger.info("all user are crawled.skip...");
				return;
			}
			long uid = user.getUid();
			List<User> followers = Utils.getFollowers(client, uid);
			logger.info("collect {} followers for user {}", followers.size(), uid);
			logger.debug("collect followers : {}", followers);
			Utils.closePage(client);
			
			userService.addCrawledFollowers(user, followers);
		} catch (Exception e) {
			logger.error("crawl user followers error...", e);
		}
	}

	private class Task implements Runnable{
		@Override
		public void run() {
			//随机停留
			int rand = new Random().nextInt(10);
			try {
				Thread.sleep(rand * 1000);
			} catch (InterruptedException e) {
			}
			crawlUserFollowers();
		}
		
	}
}
