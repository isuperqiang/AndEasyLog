package com.richie.easylog;

import android.content.Context;

/**
 * 日志配置
 * Logger config
 *
 * @author Richie on 2018.01.10
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
     * Directory to cache log file, default in app external cache dir
     */
    private String mLogFileDir;
    /**
     * 文件日志占用的最大空间
     * Max size of directory to cache log file
     */
    private long mMaxFilesSize;
    private Context mContext;

    public boolean isLogcatEnabled() {
        return mLogcatEnabled;
    }

    public boolean isLogFileEnabled() {
        return mLogFileEnabled;
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

    public long getMaxFilesSize() {
        return mMaxFilesSize;
    }

    public static class Builder {
        private boolean mLogcatEnabled = true;
        private boolean mLogFileEnabled;
        // Default max size 10M
        private long mMaxFilesSize = 10 * 1024 * 1024L;
        private String mLogFileDir;
        private Context mContext;

        public LoggerConfig build() {
            LoggerConfig loggerConfig = new LoggerConfig();
            loggerConfig.mContext = mContext;
            loggerConfig.mLogcatEnabled = mLogcatEnabled;
            loggerConfig.mLogFileEnabled = mLogFileEnabled;
            loggerConfig.mLogFileDir = mLogFileDir;
            loggerConfig.mMaxFilesSize = mMaxFilesSize;
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

        public Builder maxFilesSize(long maxFilesSize) {
            mMaxFilesSize = maxFilesSize;
            return this;
        }

    }

}
