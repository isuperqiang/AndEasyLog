package com.easy.log;

/**
 * @author Richie on 2017.05.20
 *         日志配置
 */
public class LogConfig {
    /**
     * 日志开关
     */
    private static boolean logEnable = true;

    public static boolean isLogEnable() {
        return logEnable;
    }

    public static void setLogEnable(boolean enable) {
        logEnable = enable;
    }
}
