package com.richie.easylog;

import android.content.Context;

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
    private boolean mLogcatEnabled;
    /**
     * 文件日志开关，默认关闭
     * Output to file, default false
     */
    private boolean mLogFileEnabled;
    /**
     * 日志文件保存的目录，默认存放外置 cache 目录下
     * Directory to cache log file, default in app external cache dir.
     */
    private String mLogFileDir;
    private Context mContext;

    public boolean isLogcatEnabled() {
        return mLogcatEnabled;
    }

    public void setLogcatEnabled(boolean logEnabled) {
        mLogcatEnabled = logEnabled;
    }

    public boolean isLogFileEnabled() {
        return mLogFileEnabled;
    }

    public void setLogFileEnabled(boolean logFileEnabled) {
        mLogFileEnabled = logFileEnabled;
    }

    public String getLogFileDir() {
        return mLogFileDir;
    }

    public void setLogFileDir(String logFileDir) {
        mLogFileDir = logFileDir;
    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public static class Builder {
        private boolean mLogcatEnabled = true;
        private boolean mLogFileEnabled = false;
        private String mLogFileDir;
        private Context mContext;

        public LoggerConfig build() {
            LoggerConfig loggerConfig = new LoggerConfig();
            loggerConfig.mContext = mContext;
            loggerConfig.mLogcatEnabled = mLogcatEnabled;
            loggerConfig.mLogFileEnabled = mLogFileEnabled;
            loggerConfig.mLogFileDir = mLogFileDir;
            return loggerConfig;
        }

        public Builder context(Context context) {
            mContext = context;
            return this;
        }

        public Builder logcatEnabled(boolean logcatEnabled) {
            mLogcatEnabled = logcatEnabled;
            return this;
        }

        public Builder logFileEnabled(boolean logFileEnabled) {
            mLogFileEnabled = logFileEnabled;
            return this;
        }

        public Builder logFireDir(String logFileDir) {
            mLogFileDir = logFileDir;
            return this;
        }
    }
}
