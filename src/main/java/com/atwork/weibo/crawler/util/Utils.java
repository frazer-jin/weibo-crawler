package com.atwork.weibo.crawler.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atwork.weibo.crawler.EndPoint;
import com.atwork.weibo.crawler.domain.User;
import com.atwork.weibo.crawler.domain.UserDetail;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class Utils {

	private static Logger logger = LoggerFactory.getLogger(Utils.class);
	
	private static SimpleDateFormat DF_CREATE = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DF_BIRTH = new SimpleDateFormat("yyyy年MM月dd日");

	private static ThreadLocal<WebClient> threadLocal = new ThreadLocal<>();

	public static WebClient getCrawlerClient(String username, String password) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		WebClient client = threadLocal.get();
		if (null == client) {
			logger.info("create an WebClient instance for this thread {}", Thread.currentThread().getName());
			client = createWebClient();
			threadLocal.set(client);
			login(client, username, password);
		}

		return client;
	}

	private static void login(WebClient client, String username, String password) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(EndPoint.LOGIN_ENDPOINT);

		HtmlTextInput usernameEl = page.getFirstByXPath("//*[@id=\"loginName\"]");
		HtmlPasswordInput passwordEl = page.getFirstByXPath("//*[@id=\"loginPassword\"]");
		HtmlAnchor submit = page.getFirstByXPath("//*[@id=\"loginAction\"]");

		usernameEl.setText(username);
		passwordEl.setText(password);

		page = submit.click();
	}
	
	public static void closePage(WebClient client){
		client.close();
	}
	
	public static List<User> getFollowers(WebClient client, long uid) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		List<User> users = new ArrayList<>();
		
		String url = String.format(EndPoint.FOLLOW_ENDPOINT, String.valueOf(uid));
		logger.info("crawler followers url: {}", url);
		
		HtmlPage page = client.getPage(url);

		// 确保页面内容已经加载完毕
		Object object = page.getFirstByXPath("//*[@id=\"Pl_Official_HisRelation__62\"]/div/div/div/div[2]");
		int retry = 5;
		while(retry > 0 && object == null){
			// 停留片刻
			synchronized (page) {
				try {
					page.wait(2000);
				} catch (InterruptedException e) {
				}
			}
			retry --;
			object = page.getFirstByXPath("//*[@id=\"Pl_Official_HisRelation__62\"]/div/div/div/div[2]");
		}
		
		@SuppressWarnings("unchecked")
		List<HtmlListItem> list = (List<HtmlListItem>) page.getByXPath("//*[@id=\"Pl_Official_HisRelation__62\"]/div/div/div/div[2]/div[1]/ul/li");
		if(null != list && !list.isEmpty()){
			for(HtmlListItem item : list){
				String info = item.getAttribute("action-data");
				String[] infoArr = info.split("&");
				String id = infoArr[0].substring(4);
				String name = infoArr[1].substring(6);
				char sex = "f".equals(infoArr[2].substring(4)) ? 'f':'m';
				
				User user = new User(Long.parseLong("100505"+ id), name, sex);
				users.add(user);
			}
		}
		
		return users;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean getUserDetails(WebClient client, UserDetail user) throws FailingHttpStatusCodeException, MalformedURLException, IOException, ParseException{
		boolean updated = false;
		
		String url = String.format(EndPoint.DETAIL_ENDPOINT, String.valueOf(user.getUid()));
		logger.info("crawler user details url: {}", url);
		
		HtmlPage page = client.getPage(url);
		
		// 确保页面内容已经加载完毕
		Object object = page.getFirstByXPath("//*[@id=\"Pl_Official_PersonalInfo__60\"]/div[1]");
		int retry = 5;
		while(retry > 0 && object == null){
			// 停留片刻
			synchronized (page) {
				try {
					page.wait(2000);
				} catch (InterruptedException e) {
				}
			}
			retry --;
			object = page.getFirstByXPath("//*[@id=\"Pl_Official_PersonalInfo__60\"]/div[1]");
		}
		
		List<HtmlListItem> list = (List<HtmlListItem>) page.getByXPath("//*[@id=\"Pl_Official_PersonalInfo__60\"]/div[1]/div/div[2]/div/ul/li");
																		//*[@id="Pl_Official_PersonalInfo__60"]/div[1]/div/div[2]/div/ul/li[1]
		if(null != list && !list.isEmpty()){
			
			Map<String, String> hash = new HashMap<>();
			for(int i=0; i< list.size(); i++){
				HtmlListItem item = list.get(i);
				
				DomNode labelNode = item.getFirstElementChild();
				DomNode valueNode = item.getLastElementChild();
				
				if(null == labelNode.getTextContent() || null == valueNode.getTextContent()){
					continue;
				}
				hash.put(labelNode.getTextContent().trim(), valueNode.getTextContent().trim());
			}
		
			list = (List<HtmlListItem>) page.getByXPath("//*[@id=\"Pl_Official_PersonalInfo__60\"]/div[2]/div/div[2]/div/ul/li");
			if(null != list && !list.isEmpty()){
				
				for(HtmlListItem item : list){
					
					DomNode labelNode = item.getFirstElementChild();
					DomNode valueNode = item.getLastElementChild();
					
					if(null == labelNode.getTextContent() || null == valueNode.getTextContent()){
						continue;
					}
					hash.put(labelNode.getTextContent().trim(), valueNode.getTextContent().trim().replaceAll("\\s+"," "));
				}
			}
			logger.info("get user detail info hash: {}", hash);
			for(Entry<String, String> entry : hash.entrySet()){
				if("昵称：".equals(entry.getKey())){
					user.setNickName(entry.getValue());
				}else if("所在地：".equals(entry.getKey())){
					user.setAddress(entry.getValue());
				}else if("性别：".equals(entry.getKey())){
					user.setSex("男".equals(entry.getValue()) ? 'm' :'f');
				}else if("注册时间：".equals(entry.getKey())){
					try {
						user.setCreate(new Date(DF_CREATE.parse(entry.getValue()).getTime()));
					} catch (Exception e) {
						logger.error("parse date error.", e);
					}
				}else if("大学：".equals(entry.getKey())){
					user.setCollege(entry.getValue());
				}else if("生日：".equals(entry.getKey())){
					try {
						user.setBirth(new Date(DF_BIRTH.parse(entry.getValue()).getTime()));
					} catch (Exception e) {
						logger.error("parse date error.", e);
					}
				}else if ("简介：".equals(entry.getKey())){
					user.setMemo(entry.getValue());
				}
			}
			updated = true;
		}
		return updated;
	}

	private static WebClient createWebClient() {
		WebClient webClient = new WebClient(BrowserVersion.CHROME);

		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.waitForBackgroundJavaScript(10000);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());

		webClient.addRequestHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

		return webClient;
	}
}
