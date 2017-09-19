package com.richie.easylog;

/**
 * 日志工厂
 */
public class LoggerFactory {
    private static final ILogger sEmptyLogger = new EmptyLogger();
    /**
     * 日志开关
     */
    private static final boolean LOG_ENABLED = true;

    /**
     * 根据 tag 获取日志
     *
     * @param tag tag
     * @return log
     */
    public static ILogger getLogger(String tag) {
        if (isLogEnabled() && !LogUtils.isEmpty(tag)) {
            return new AndroidLogger(tag);
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
        return LOG_ENABLED;
    }
}
