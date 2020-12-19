package com.richie.easylog;

import android.content.Context;
import android.util.Log;

/**
 * 日志配置
 * Logger config
 *
 * @author Richie on 2018.01.10
 */
public final class LoggerConfig {
    /**
     * Directory's default max size 10M
     * 文件夹默认大小 10M
     */
    private static final long DEFAULT_DIRECTORY_SIZE = 10 * 1024 * 1024L;
    /**
     * Logcat 日志开关，默认关闭
     * Logcat switch, default false
     */
    private boolean mLogcatEnabled;
    /**
     * 文件日志开关，默认关闭
     * Output to file, default false
     */
    private boolean mLogFileEnabled;
    /**
     * 日志文件保存的目录，默认存放外置 cache 目录下
     * Directory to cache log file, default is in app external cache dir
     */
    private String mLogFileDir;
    /**
     * 文件日志占用的最大空间
     * Max size of directory to cache log file
     */
    private long mMaxFileSize;
    private Context mContext;
    /**
     * 日志级别，默认关闭
     */
    private int mLogLevel = DEBUG;

    /**
     * Log level
     */
    public static final int VERBOSE = Log.VERBOSE;
    public static final int DEBUG = Log.DEBUG;
    public static final int INFO = Log.INFO;
    public static final int WARN = Log.WARN;
    public static final int ERROR = Log.ERROR;
    public static final int OFF = 7;

    LoggerConfig() {
    }

    public boolean isLogcatEnabled() {
        return mLogcatEnabled;
    }

    public boolean isLogFileEnabled() {
        return mLogFileEnabled;
    }

    public String getLogFileDir() {
        return mLogFileDir;
    }

    public Context getContext() {
        return mContext;
    }

    public long getMaxFileSize() {
        return mMaxFileSize;
    }

    public boolean isLoggable(int level) {
        return level >= mLogLevel;
    }

    public static class Builder {
        private boolean mLogcatEnabled = false;
        private boolean mLogFileEnabled = false;
        private long mMaxFileSize = DEFAULT_DIRECTORY_SIZE;
        private String mLogFileDir;
        private Context mContext;
        private int mLogLevel = DEBUG;

        public LoggerConfig build() {
            LoggerConfig loggerConfig = new LoggerConfig();
            loggerConfig.mContext = mContext.getApplicationContext();
            loggerConfig.mLogcatEnabled = mLogcatEnabled;
            loggerConfig.mLogFileEnabled = mLogFileEnabled;
            loggerConfig.mLogFileDir = mLogFileDir;
            loggerConfig.mMaxFileSize = mMaxFileSize;
            loggerConfig.mLogLevel = mLogLevel;
            if (mLogFileEnabled && LoggerUtils.isEmpty(mLogFileDir)) {
                loggerConfig.mLogFileDir = LoggerUtils.getLogFileDir(mContext).getAbsolutePath();
            }
            if (mMaxFileSize <= 0) {
                mMaxFileSize = DEFAULT_DIRECTORY_SIZE;
            }
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

        public Builder maxFileSize(long maxFileSize) {
            mMaxFileSize = maxFileSize;
            return this;
        }

        public Builder logLevel(int logLevel) {
            mLogLevel = logLevel;
            return this;
        }
    }
}
