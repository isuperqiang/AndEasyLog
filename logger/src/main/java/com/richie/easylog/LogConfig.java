package com.richie.easylog;

/**
 * @author Richie on 2018.01.10
 * 日志配置
 */
public final class LogConfig {
    /**
     * Logcat 日志开关
     */
    private static boolean sLogcatEnabled = false;
    /**
     * 文件日志开关
     */
    private static boolean sLogFileEnabled = false;
    /**
     * 日志文件保存的目录
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

    public static void setLogFileConfig(boolean logFileEnabled, String logFileDir) {
        sLogFileEnabled = logFileEnabled;
        sLogFileDir = logFileDir;
    }
}
