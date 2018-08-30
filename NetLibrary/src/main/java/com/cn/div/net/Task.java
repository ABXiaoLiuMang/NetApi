package com.cn.div.net;

import java.util.HashMap;
import java.util.Map;

/**
 * TOP API请求业务参数
 * 
 */
public class Task {

	private Map<String, String> params = new HashMap<String, String>();
	private Map<String, FileItem> attachments = new HashMap<String, FileItem>();
	/** 任务的ID **/
	private int mTaskId;
	private String url;
	private String result;
	private boolean exception = false;
	private boolean isPost = false;
	
	public Task(String url,int taskId){
		this.url = url;
		this.mTaskId = taskId;
	}
	
	public Task(String url,int taskId,boolean isPost){
		this.url = url;
		this.mTaskId = taskId;
		this.isPost = isPost;
	}


	/**
	 * 添加业务参数
	 * 
	 * @param key
	 * @param value
	 */
	public void addParam(String key, String value) {
		params.put(key, value);
	}
	
	/**
	 * 添加业务参数
	 * 
	 * @param key
	 * @param value
	 */
	public void addParam(String key, int value) {
		params.put(key, String.valueOf(value));
	}

	/**
	 * 获取已添加的业务参数
	 * 
	 * @param key
	 * @return
	 */
	public String getParam(String key) {
		return params.get(key);
	}

	/**
	 * 删除已添加的业务参数
	 * 
	 * @param key
	 */
	public void removeParam(String key) {
		params.remove(key);
	}

	/**
	 * 添加附件
	 * 
	 * @param key
	 * @param file
	 */
	public void addAttachment(String key, FileItem file) {
		if (file == null) {
			return;
		}
		attachments.put(key, file);
	}

	/**
	 * 获取已添加的附件
	 * 
	 * @param key
	 * @return
	 */
	public FileItem getAttachment(String key) {
		return attachments.get(key);
	}

	/**
	 * 删除已添加的附件
	 * 
	 * @param key
	 */
	public void removeAttachment(String key) {
		attachments.remove(key);
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public Map<String, FileItem> getAttachments() {
		return attachments;
	}

	public void setAttachments(Map<String, FileItem> attachment) {
		this.attachments = attachment;
	}


	public String getResult() {
		return result;
	}


	public void setResult(String result) {
		this.result = result;
	}


	public boolean isException() {
		return exception;
	}


	public void setException(boolean exception) {
		this.exception = exception;
	}


	public int getmTaskId() {
		return mTaskId;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public boolean isPost() {
		return isPost;
	}


	public void setPost(boolean isPost) {
		this.isPost = isPost;
	}
	
}
