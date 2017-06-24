package com.richie.easylog;

/**
 * 日志配置
 */
public class LogConfig {
    /**
     * 日志开关
     */
    private static boolean logEnable = true;

    static boolean isLogEnable() {
        return logEnable;
    }

    public static void setLogEnable(boolean enable) {
        logEnable = enable;
    }
}
