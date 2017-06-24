package com.richie.easylog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志工厂
 */
public class LoggerFactory {
    private static final Map<String, ILogger> logMaps = new ConcurrentHashMap<>();
    private static final ILogger sEmptyLogger = new EmptyLogger();

    /**
     * 根据 tag 获取日志
     *
     * @param tag tag
     * @return log
     */
    public static ILogger getLogger(String tag) {
        ILogger log;
        if (!LogUtils.isEmpty(tag)) {
            if (LogConfig.isLogEnable()) {
                log = logMaps.get(tag);
                if (log == null) {
                    log = new AndroidLogger(tag);
                    logMaps.put(tag, log);
                }
                return log;
            } else {
                return sEmptyLogger;
            }
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
        ILogger log;
        String tag = clazz.getSimpleName();
        if (!LogUtils.isEmpty(tag)) {
            if (LogConfig.isLogEnable()) {
                log = logMaps.get(tag);
                if (log == null) {
                    log = new AndroidLogger(tag);
                    logMaps.put(tag, log);
                }
                return log;
            } else {
                return sEmptyLogger;
            }
        } else {
            return sEmptyLogger;
        }
    }

}
