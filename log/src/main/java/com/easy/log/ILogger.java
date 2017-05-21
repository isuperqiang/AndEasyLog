package com.easy.log;

/**
 * 日志接口
 */
public interface ILogger {

    /**
     * 啰嗦级别的输出
     *
     * @param message 消息
     * @param params  参数
     */
    void verbose(String message, Object... params);

    /**
     * 调试级别的输出
     *
     * @param message 消息
     * @param params  参数
     */
    void debug(String message, Object... params);

    /**
     * 信息级别的输出
     *
     * @param message 消息
     * @param params  参数
     */
    void info(String message, Object... params);

    /**
     * 警告级别的输出
     *
     * @param throwable 异常
     */
    void warn(Throwable throwable);

    /**
     * 警告级别的输出
     *
     * @param message   消息
     * @param throwable 异常
     */
    void warn(String message, Throwable throwable);

    /**
     * 警告级别的输出
     *
     * @param message 消息
     * @param params  参数
     */
    void warn(String message, Object... params);

    /**
     * 错误级别的输出
     *
     * @param throwable 异常
     */
    void error(Throwable throwable);

    /**
     * 错误级别的输出
     *
     * @param message   消息
     * @param throwable 异常
     */
    void error(String message, Throwable throwable);

    /**
     * 错误级别的输出
     *
     * @param message 消息
     * @param params  参数
     */
    void error(String message, Object... params);

    /**
     * 格式化打印 Json
     *
     * @param json Json 字符串
     */
    void json(String json);

    /**
     * 格式化打印 Xml
     *
     * @param xml Xml 文本
     */
    void xml(String xml);

    enum LOG_LEVEL {
        v, d, i, w, e
    }
}
