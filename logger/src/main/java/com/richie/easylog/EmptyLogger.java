package com.richie.easylog;

/**
 * ILogger 空实现，什么也不做
 * ILogger null implementation, do nothing.
 *
 * @author Richie on 2018.01.10
 */
final class EmptyLogger implements ILogger {

    @Override
    public void verbose(String message, Object... params) {

    }

    @Override
    public void debug(String message, Object... params) {

    }

    @Override
    public void info(String message, Object... params) {

    }

    @Override
    public void warn(Throwable throwable) {

    }

    @Override
    public void warn(String message, Throwable throwable) {

    }

    @Override
    public void warn(String message, Object... params) {

    }

    @Override
    public void error(Throwable throwable) {

    }

    @Override
    public void error(String message, Throwable throwable) {

    }

    @Override
    public void error(String message, Object... params) {

    }

    @Override
    public String json(String json) {
        return null;
    }

    @Override
    public String xml(String xml) {
        return null;
    }

    @Override
    public String stackTrack(Throwable throwable) {
        return null;
    }
}
