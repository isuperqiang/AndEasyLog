package com.richie.easylog;

/**
 * 日志接口
 * Logger interface
 *
 * @author Richie on 2018.01.10
 */
public interface ILogger {

    /**
     * 啰嗦级别的输出
     * verbose print
     *
     * @param message 消息
     * @param params  参数
     */
    void verbose(String message, Object... params);

    /**
     * 调试级别的输出
     * debug print
     *
     * @param message 消息
     * @param params  参数
     */
    void debug(String message, Object... params);

    /**
     * 信息级别的输出
     * info print
     *
     * @param message 消息
     * @param params  参数
     */
    void info(String message, Object... params);

    /**
     * 警告级别的输出
     * warn print
     *
     * @param throwable 异常
     */
    void warn(Throwable throwable);

    /**
     * 警告级别的输出
     * warn print
     *
     * @param message   消息
     * @param throwable 异常
     */
    void warn(String message, Throwable throwable);

    /**
     * 警告级别的输出
     * warn print
     *
     * @param message 消息
     * @param params  参数
     */
    void warn(String message, Object... params);

    /**
     * 错误级别的输出
     * error print
     *
     * @param throwable 异常
     */
    void error(Throwable throwable);

    /**
     * 错误级别的输出
     * error print
     *
     * @param message   消息
     * @param throwable 异常
     */
    void error(String message, Throwable throwable);

    /**
     * 错误级别的输出
     * error print
     *
     * @param message 消息
     * @param params  参数
     */
    void error(String message, Object... params);

    /**
     * 格式化打印 JSON
     * JSON format print
     *
     * @param json JSON 字符串
     */
    void json(String json);

    /**
     * 格式化打印 XML
     * XML format print
     *
     * @param xml XML 文本
     */
    void xml(String xml);
}
