package com.richie.easylog;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志工厂
 */
public class LoggerFactory {
    private static final String DEFAULT_TAG = "logger";
    private static ILogger sEmptyLogger = new EmptyLogger();
    private static ConcurrentHashMap<String, ILogger> sLoggerMap;
    /**
     * 日志开关
     */
    private static boolean sLogEnabled = true;

    /**
     * 根据 tag 获取日志
     *
     * @param tag tag
     * @return log
     */
    public static ILogger getLogger(String tag) {
        if (isLogEnabled()) {
            if (LogUtils.isEmpty(tag)) {
                tag = DEFAULT_TAG;
            }
            if (sLoggerMap == null) {
                sLoggerMap = new ConcurrentHashMap<>();
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
     *
     * @param clazz 类名
     * @return log
     */
    public static ILogger getLogger(Class clazz) {
        return getLogger(clazz.getSimpleName());
    }

    public static boolean isLogEnabled() {
        return sLogEnabled;
    }

    public static void setLogEnabled(boolean logEnabled) {
        sLogEnabled = logEnabled;
    }

}
