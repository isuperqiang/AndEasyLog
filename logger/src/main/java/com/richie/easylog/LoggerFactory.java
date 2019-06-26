package com.richie.easylog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志工厂
 * Logger factory
 *
 * @author Richie on 2018.01.10
 */
public final class LoggerFactory {
    static final String DEFAULT_TAG = "logger";
    private static final ILogger EMPTY_LOGGER = new EmptyLogger();
    private static final Map<String, ILogger> LOGGER_CACHE = new ConcurrentHashMap<>(128);
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
            throw new IllegalStateException("LoggerConfig isn't initialized");
        }
        if (sLoggerConfig.isLogcatEnabled() || sLoggerConfig.isLogFileEnabled()) {
            if (LoggerUtils.isEmpty(tag)) {
                tag = DEFAULT_TAG;
            }
            ILogger logger = LOGGER_CACHE.get(tag);
            if (logger == null) {
                logger = new AndroidLogger(tag);
                LOGGER_CACHE.put(tag, logger);
            }
            return logger;
        } else {
            return EMPTY_LOGGER;
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

    public static LoggerConfig getLoggerConfig() {
        return sLoggerConfig;
    }

}