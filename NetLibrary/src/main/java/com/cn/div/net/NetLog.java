package com.cn.div.net;

import android.text.TextUtils;
import android.util.Log;

public class NetLog {

    public static String customTagPrefix = "Dream"; // 自定义Tag的前缀，可以是作者名

    private NetLog() {
    }

    // 容许打印日志的类型，默认是true，设置为false则不打印
    public static boolean allowD = true;

    private static String generateTag(StackTraceElement caller, String mtag) {
        String tag = "%s.%s(Line:%d)"; // 占位符
        String callerClazzName = caller.getClassName(); // 获取到类名
        callerClazzName = callerClazzName.substring(callerClazzName
                .lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(),
                caller.getLineNumber()); // 替换
        tag = TextUtils.isEmpty(mtag) ? customTagPrefix + ":"
                + tag :mtag + ":"+ tag ;
        return tag;
    }


    public static void d(String content) {
        if (!allowD)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller,null);
        Log.d(tag, content);
    }
    
    public static void d(String mtag, String content) {
    	if (!allowD)
    		return;
    	StackTraceElement caller = getCallerStackTraceElement();
    	String tag = generateTag(caller,mtag);
    	Log.d(tag, content);
    }

  
    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }
    
}
