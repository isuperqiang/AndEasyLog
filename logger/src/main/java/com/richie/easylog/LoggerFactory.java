package com.richie.easylog;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志工厂
 * Logger factory
 *
 * @author Richie on 2018.01.10
 */
public final class LoggerFactory {
    private static final String DEFAULT_TAG = "logger";
    private static ILogger sEmptyLogger = new EmptyLogger();
    private static Map<String, ILogger> sLoggerCache = new ConcurrentHashMap<>(128);
    @Deprecated // use normal application context instead
    private static Context sAppContext;
    private static LoggerConfig sLoggerConfig;

    public static void init(LoggerConfig loggerConfig) {
        if (loggerConfig == null) {
            throw new NullPointerException("LoggerConfig can't be null");
        }
        sLoggerConfig = loggerConfig;
    }

    /**
     * 根据 tag 获取日志
     * Get logger by tag
     *
     * @param tag tag
     * @return log
     */
    public static ILogger getLogger(String tag) {
        if (sLoggerConfig == null) {
            throw new IllegalStateException("LoggerConfig did't initialized");
        }
        if (sLoggerConfig.isLogcatEnabled() || sLoggerConfig.isLogFileEnabled()) {
            if (LoggerUtils.isEmpty(tag)) {
                tag = DEFAULT_TAG;
            }
            ILogger logger = sLoggerCache.get(tag);
            if (logger == null) {
                logger = new AndroidLogger(tag);
                sLoggerCache.put(tag, logger);
            }
            return logger;
        } else {
            return sEmptyLogger;
        }
    }

    /**
     * 以类名为 Tag 获取日志
     * Get logger by class name
     *
     * @param clazz 类名
     * @return log
     */
    public static ILogger getLogger(Class clazz) {
        if (clazz == null) {
            clazz = Object.class;
        }
        return getLogger(clazz.getSimpleName());
    }

    /**
     * 获取 Application Context
     * Get application context
     *
     * @return context
     */
    @SuppressLint("PrivateApi")
    @Deprecated
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
            } catch (Exception e) {
                if (sLoggerConfig.isLogcatEnabled()) {
                    Log.e("LoggerFactory", "getAppContext", e);
                }
            }
        }
        return sAppContext;
    }

    static LoggerConfig getLoggerConfig() {
        return sLoggerConfig;
    }
}