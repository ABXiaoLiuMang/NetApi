package com.cn.div.net;


/**
 * 异步网络请求
 * 
 * @author Administrator
 *
 */
public class HttpRequestAsyncTask extends UXAsyncTask<Void, Void, Task> {
	private RequestCallBack responselistener;
	private Task task;

	public HttpRequestAsyncTask(Task task ,RequestCallBack responselistener) {
		this.task = task;
		this.responselistener = responselistener;
	}

	/**
	 * 后台线程
	 */
	@Override
	protected Task doInBackground(Void... params) {
		final String url = task.getUrl();
		String jsonStr;
		try {
			if (task.isPost()) {
				 jsonStr = WebUtils.doPost(url,task.getParams(),task.getAttachments());
			} else {
				 jsonStr = WebUtils.doGet(url,task.getParams());
			}
			
		}catch(Exception e){
			try {
				if (task.isPost()) {
					 jsonStr = WebUtils.doPost(url,task.getParams(),task.getAttachments());
				} else {
					 jsonStr = WebUtils.doGet(url,task.getParams());
				}
			}catch(Exception e1){
				task.setException(true);
				task.setResult(e1.toString());
				return task;
			}
		}
		
		if (jsonStr == null) {
			task.setException(true);
			return task;
		}
		
		
		task.setResult(jsonStr);
		return task;
	}

	/**
	 * 前台线程
	 */
	@Override
	protected void onPostExecute(Task result) {
		if (result.isException()) {
			if(NetLog.allowD){
				NetLog.d("Net","tag="+result.getmTaskId()+" result=>onException");
			}
			responselistener.onException(result.getmTaskId());
		} else {
			if(NetLog.allowD){
				NetLog.d("Net","tag="+result.getmTaskId()+" result=>"+result.getResult());
			}
			responselistener.onComplete(result.getResult(), result.getmTaskId());
		}
	}
	

}
