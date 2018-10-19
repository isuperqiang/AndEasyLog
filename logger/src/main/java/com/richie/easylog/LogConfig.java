package com.richie.easylog;

/**
 * @author Richie on 2018.01.10
 * 建议使用 LoggerConfig
 */
@Deprecated
public final class LogConfig {

    public static void setLogcatEnabled(boolean logEnabled) {
        LoggerConfig.setLogcatEnabled(logEnabled);
    }

    public static void setLogFileConfig(boolean logFileEnabled, String logFileDir) {
        LoggerConfig.setLogFileEnabled(logFileEnabled);
        LoggerConfig.setLogFileDir(logFileDir);
    }
}
