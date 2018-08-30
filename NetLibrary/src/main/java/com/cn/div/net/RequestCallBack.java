/**
 * 
 */
package com.cn.div.net;


/**
 * API调用的事件监听器
 * 
 * @author junyan.hj
 * 
 */
public interface RequestCallBack {
	/**
	 * API调用成功后返回以String对象方式通知监听
	 * 
	 */
	void onComplete(String str, int tag);

	/**
	 * 出现网络问题等未知异常时会回调此方法
	 * 
	 */
	void onException(int tag);
}
