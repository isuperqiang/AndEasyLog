package com.richie.easylog;

/**
 * Android 日志打印
 * Android logger
 *
 * @author Richie on 2018.01.10
 */
final class AndroidLogger implements ILogger {
    /**
     * 标签
     * Log tag
     */
    private final String mTag;

    AndroidLogger(String tag) {
        mTag = tag;
    }

    @Override
    public void verbose(String message, Object... params) {
        LoggerUtils.log(LoggerConfig.VERBOSE, mTag, message, null, params);
    }

    @Override
    public void debug(String message, Object... params) {
        LoggerUtils.log(LoggerConfig.DEBUG, mTag, message, null, params);
    }

    @Override
    public void info(String message, Object... params) {
        LoggerUtils.log(LoggerConfig.INFO, mTag, message, null, params);
    }

    @Override
    public void warn(Throwable throwable) {
        warn(null, throwable);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        LoggerUtils.log(LoggerConfig.WARN, mTag, message, throwable);
    }

    @Override
    public void warn(String message, Object... params) {
        LoggerUtils.log(LoggerConfig.WARN, mTag, message, null, params);
    }

    @Override
    public void error(Throwable throwable) {
        error(null, throwable);
    }

    @Override
    public void error(String message, Throwable throwable) {
        LoggerUtils.log(LoggerConfig.ERROR, mTag, message, throwable);
    }

    @Override
    public void error(String message, Object... params) {
        LoggerUtils.log(LoggerConfig.ERROR, mTag, message, null, params);
    }

    @Override
    public String json(String json) {
        return LoggerUtils.formatJson(json);
    }

    @Override
    public String xml(String xml) {
        return LoggerUtils.formatXml(xml);
    }

    @Override
    public String stackTrack(Throwable throwable) {
        return LoggerUtils.getStackTraceString(throwable);
    }
}