package com.richie.easylog;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author richie
 *         日志工厂
 */
public class LoggerFactory {
    private static final String DEFAULT_TAG = "logger";
    private static ILogger sEmptyLogger = new EmptyLogger();
    private static ConcurrentHashMap<String, ILogger> sLoggerMap = new ConcurrentHashMap<>();

    /**
     * 根据 tag 获取日志
     *
     * @param tag tag
     * @return log
     */
    public static ILogger getLogger(String tag) {
        if (LogConfig.isLogEnabled()) {
            if (LogUtils.isEmpty(tag)) {
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
     *
     * @param clazz 类名
     * @return log
     */
    public static ILogger getLogger(Class clazz) {
        return getLogger(clazz.getSimpleName());
    }

}
