package com.richie.easylog;

/**
 * @author Richie on 2018.01.10
 * 日志配置
 * Logger config
 */
public final class LoggerConfig {
    /**
     * Logcat 日志开关，默认打开
     * Logcat switch, default true
     */
    private static boolean sLogcatEnabled = true;
    /**
     * 文件日志开关，默认关闭
     * Output Log to file, default false
     */
    private static boolean sLogFileEnabled = false;
    /**
     * 日志文件保存的目录，默认存放外置 cache 目录下
     * Dir to save log file, default app external cache dir.
     */
    private static String sLogFileDir;

    public static boolean isLogcatEnabled() {
        return sLogcatEnabled;
    }

    public static void setLogcatEnabled(boolean logEnabled) {
        sLogcatEnabled = logEnabled;
    }

    public static boolean isLogFileEnabled() {
        return sLogFileEnabled;
    }

    public static String getLogFileDir() {
        return sLogFileDir;
    }

    public static void setLogFileEnabled(boolean logFileEnabled) {
        sLogFileEnabled = logFileEnabled;
    }

    public static void setLogFileDir(String logFileDir) {
        sLogFileDir = logFileDir;
    }
}
