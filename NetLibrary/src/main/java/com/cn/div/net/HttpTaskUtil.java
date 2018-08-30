package com.cn.div.net;

import java.io.UnsupportedEncodingException;


/***
 * 网络请求工具类
 *
 */
public class HttpTaskUtil {

	private static HttpTaskUtil instance;
	private int currentVersion = android.os.Build.VERSION.SDK_INT;

	public static HttpTaskUtil getInstance() {
		if (instance == null) {
			instance = new HttpTaskUtil();
		}
		return instance;
	}

	public void doHttpTask(Task task, RequestCallBack responselistener) {
		if(NetLog.allowD){
			try {
				NetLog.d("Net","tag="+task.getmTaskId()+" url=>"+task.getUrl()+WebUtils.buildQuery(task.getParams(), null));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		new HttpRequestAsyncTask(task, responselistener).execute();
	}
}
