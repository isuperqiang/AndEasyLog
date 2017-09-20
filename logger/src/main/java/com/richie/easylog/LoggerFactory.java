package com.richie.easylog;

/**
 * 日志工厂
 */
public class LoggerFactory {
    private static ILogger sEmptyLogger;
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
        if (isLogEnabled() && !LogUtils.isEmpty(tag)) {
            return new AndroidLogger(tag);
        } else {
            if (sEmptyLogger == null) {
                sEmptyLogger = new EmptyLogger();
            }
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
