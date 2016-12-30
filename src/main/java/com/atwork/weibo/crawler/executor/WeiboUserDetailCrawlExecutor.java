package com.atwork.weibo.crawler.executor;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
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

import com.atwork.weibo.crawler.domain.UserDetail;
import com.atwork.weibo.crawler.service.UserService;
import com.atwork.weibo.crawler.util.Utils;
import com.gargoylesoftware.htmlunit.WebClient;

@Component
public class WeiboUserDetailCrawlExecutor implements InitializingBean, DisposableBean {

	private static Logger logger = LoggerFactory.getLogger(WeiboUserDetailCrawlExecutor.class);
	
	@Value("${common.weibo.username}")
	private String username;
	@Value("${common.weibo.password}")
	private String password;
	@Value("${common.user.detail.bulk}")
	private int thread;

	private ScheduledExecutorService scheduler = null;
	private ExecutorService userDetailCrawler;

	@Autowired
	private UserService userService;

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("start WeiboUserCrawlService scheduler...username {}, password {}", username, password);
		startScheduleExecutor();
	}

	@Override
	public void destroy() throws Exception {
		logger.info("shutting down scheduler...");
		if (null != scheduler && !scheduler.isShutdown()) {
			scheduler.shutdown();
		}
		if (null != userDetailCrawler && !userDetailCrawler.isShutdown()) {
			userDetailCrawler.shutdown();
		}
	}

	private void startScheduleExecutor() {
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(new Task(), 10, 30, TimeUnit.SECONDS);
		userDetailCrawler = Executors.newFixedThreadPool(thread * 2);
	}

	private void crawlUserDetails() {
		List<UserDetail> users = userService.getUnCrawledUserDetail();
		if (null == users || users.isEmpty()) {
			logger.info("all user detail are filled. skip...");
			return;
		}
		for (final UserDetail user : users) {
			userDetailCrawler.submit(new Runnable() {
				@Override
				public void run() {
					boolean updated = false;
					try {
						final WebClient client = Utils.getCrawlerClient(username, password);

						updated = Utils.getUserDetails(client, user);

						logger.info("user detail updated? {}, detail: {}", updated, user);
						Utils.closePage(client);
					} catch (Exception e) {
						logger.error("crawl user detail error...", e);
					}
					if(updated){
						userService.updateUserDetail(user);
					}else{
						userService.updateUserDetailRetry(user);
					}
				}
			});
		}
	}

	private class Task implements Runnable {
		@Override
		public void run() {
			// 随机停留
			int rand = new Random().nextInt(10);
			try {
				Thread.sleep(rand * 1000);
			} catch (InterruptedException e) {
			}
			crawlUserDetails();
		}

	}

}
