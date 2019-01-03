package com.richie.easylog;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Richie
 * 日志工厂
 * Logger factory
 */
public final class LoggerFactory {
    private static final String DEFAULT_TAG = "logger";
    private static ILogger sEmptyLogger = new EmptyLogger();
    private static Map<String, ILogger> sLoggerMap = new ConcurrentHashMap<>(128);
    private static Context sAppContext;

    /**
     * 根据 tag 获取日志
     * Get logger by tag
     *
     * @param tag tag
     * @return log
     */
    public static ILogger getLogger(String tag) {
        if (LoggerConfig.isLogcatEnabled() || LoggerConfig.isLogFileEnabled()) {
            if (LoggerUtils.isEmpty(tag)) {
                tag = DEFAULT_TAG;
            }
            ILogger logger = sLoggerMap.get(tag);
            if (logger == null) {
                logger = new AndroidLogger(tag);
                sLoggerMap.put(tag, logger);
            }
            return logger;
        } else {
            return sEmptyLogger;
        }
    }

    /**
     * 以类名为 tag 获取日志
     * Get logger by class name
     *
     * @param clazz 类名
     * @return log
     */
    public static ILogger getLogger(Class clazz) {
        return getLogger(clazz.getSimpleName());
    }

    /**
     * 获取 Application Context
     * Get application context
     *
     * @return context
     */
    @SuppressLint("PrivateApi")
    static Context getAppContext() {
        if (sAppContext == null) {
            try {
                ClassLoader loader = Context.class.getClassLoader();
                Class<?> c = loader.loadClass("android.app.ActivityThread");
                Method currentActivityThreadM = c.getDeclaredMethod("currentActivityThread");
                currentActivityThreadM.setAccessible(true);
                Object currentActivityThread = currentActivityThreadM.invoke(null);
                Method getApplicationM = c.getDeclaredMethod("getApplication");
                getApplicationM.setAccessible(true);
                Application application = (Application) getApplicationM.invoke(currentActivityThread);
                sAppContext = application.getApplicationContext();
            } catch (Throwable e) {
                if (LoggerConfig.isLogcatEnabled()) {
                    e.printStackTrace();
                }
            }
        }
        return sAppContext;
    }
}