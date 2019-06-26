package com.richie.easylog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Android 日志打印
 * Android logger
 *
 * @author Richie on 2018.01.10
 */
final class AndroidLogger implements ILogger {
    private static final String TAG = "AndroidLogger";
    /**
     * 日志标签
     * Log tag
     */
    private final String tag;
    /**
     * 参数占位符
     * param placeholder
     */
    private static final String PARAMS_PLACEHOLDER = "{}";
    /**
     * 单条打印最大长度
     * log message max length
     */
    private static final int MESSAGE_MAX_LENGTH = 1024;

    AndroidLogger(String tag) {
        this.tag = tag;
    }

    @Override
    public void verbose(String message, Object... params) {
        log(Log.VERBOSE, tag, message, null, params);
    }

    @Override
    public void debug(String message, Object... params) {
        log(Log.DEBUG, tag, message, null, params);
    }

    @Override
    public void info(String message, Object... params) {
        log(Log.INFO, tag, message, null, params);
    }

    @Override
    public void warn(Throwable throwable) {
        warn(null, throwable);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        log(Log.WARN, tag, message, throwable);
    }

    @Override
    public void warn(String message, Object... params) {
        log(Log.WARN, tag, message, null, params);
    }

    @Override
    public void error(Throwable throwable) {
        error(null, throwable);
    }

    @Override
    public void error(String message, Throwable throwable) {
        log(Log.ERROR, tag, message, throwable);
    }

    @Override
    public void error(String message, Object... params) {
        log(Log.ERROR, tag, message, null, params);
    }

    @Override
    public void json(String json) {
        if (LoggerUtils.isEmpty(json)) {
            debug("Empty/Null JSON content");
            return;
        }

        try {
            json = json.trim();
            StringBuilder sb = new StringBuilder();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                sb.append("JSONObject length:")
                        .append(jsonObject.length())
                        .append("\n")
                        .append(jsonObject.toString(2));
            } else if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                sb.append("JSONArray length:")
                        .append(jsonArray.length())
                        .append("\n")
                        .append(jsonArray.toString(2));
            } else {
                sb.append("Invalid JSON:\n").append(json);
            }
            debug(sb.toString());
        } catch (Exception e) {
            warn("Invalid JSON", e);
        }
    }

    @Override
    public void xml(String xml) {
        if (LoggerUtils.isEmpty(xml)) {
            debug("Empty/Null XML content");
            return;
        }

        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            debug("XML:\n" + xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
        } catch (Exception e) {
            warn("Invalid XML", e);
        }
    }

    private void log(int level, String tag, String message, Throwable throwable, Object... params) {
        String header = createLogHeader();
        String body = createLogBody(message, params);
        if (LoggerUtils.isEmpty(body)) {
            body = "Empty/Null log message";
        }
        processLog(level, tag, header, body, throwable);
    }

    private String createLogHeader() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(Thread.currentThread().getName());
        sb.append("](");
        sb.append(getLineNumber());
        sb.append(") ");
        return sb.toString();
    }

    private String createLogBody(String message, Object[] params) {
        if (message == null) {
            if (params != null && params.length != 0) {
                return "Log format error";
            } else {
                return "Null";
            }
        }

        try {
            StringBuilder sb = new StringBuilder();
            int index = 0;
            if (params != null) {
                for (Object param : params) {
                    int j = message.indexOf(PARAMS_PLACEHOLDER, index);
                    if (j != -1) {
                        sb.append(message, index, j);
                        if (param != null) {
                            if (param.getClass().isArray()) {
                                sb.append(LoggerUtils.array2String(param));
                            } else if (param instanceof Intent) {
                                sb.append(LoggerUtils.intent2String((Intent) param));
                            } else if (param instanceof Bundle) {
                                sb.append(LoggerUtils.bundle2String((Bundle) param));
                            } else {
                                sb.append(param);
                            }
                        } else {
                            sb.append("null");
                        }
                        index = j + 2;
                    } else {
                        break;
                    }
                }
                sb.append(message, index, message.length());
            } else {
                String s = message.replace(PARAMS_PLACEHOLDER, "null");
                sb.append(s);
            }
            return sb.toString();
        } catch (Exception e) {
            if (LoggerFactory.getLoggerConfig().isLogcatEnabled()) {
                Log.e(TAG, "createLogBody", e);
            }
            return message;
        }
    }

    private int getLineNumber() {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            boolean isFound = false;
            for (StackTraceElement stackTraceElement : stackTrace) {
                if (stackTraceElement.getClassName().equals(AndroidLogger.class.getName())) {
                    isFound = true;
                    continue;
                }
                if (isFound) {
                    if (!stackTraceElement.getClassName().equals(AndroidLogger.class.getName())) {
                        return stackTraceElement.getLineNumber();
                    }
                }
            }
        } catch (Exception e) {
            if (LoggerFactory.getLoggerConfig().isLogcatEnabled()) {
                Log.e(TAG, "getLineNumber", e);
            }
        }
        return 0;
    }

    private void processLog(int level, String tag, String head, String body, Throwable throwable) {
        int length = body.length();
        if (length < MESSAGE_MAX_LENGTH) {
            printLog(level, tag, head + body, throwable);
        } else {
            int index = 0;
            int count = 0;
            String subBody;
            try {
                while (index < length) {
                    count++;
                    if (length <= index + MESSAGE_MAX_LENGTH) {
                        subBody = body.substring(index);
                    } else {
                        subBody = body.substring(index, index + MESSAGE_MAX_LENGTH);
                    }
                    index += MESSAGE_MAX_LENGTH;
                    printLog(level, tag, head + "********(" + count + ")********" + subBody, throwable);
                }
            } catch (Exception e) {
                if (LoggerFactory.getLoggerConfig().isLogcatEnabled()) {
                    Log.e(TAG, "processLog", e);
                }
            }
        }
    }

    private void printLog(int level, String tag, String message, Throwable throwable) {
        if (LoggerFactory.getLoggerConfig().isLogcatEnabled()) {
            printLogcat(level, tag, message, throwable);
        }
        if (LoggerFactory.getLoggerConfig().isLogFileEnabled()) {
            printLogFile(tag, message, throwable);
        }
    }

    private void printLogcat(int level, String tag, String message, Throwable throwable) {
        switch (level) {
            case Log.VERBOSE:
                v(tag, message, throwable);
                break;
            case Log.DEBUG:
                d(tag, message, throwable);
                break;
            case Log.INFO:
                i(tag, message, throwable);
                break;
            case Log.WARN:
                w(tag, message, throwable);
                break;
            case Log.ERROR:
                e(tag, message, throwable);
                break;
            default:
        }
    }

    private void printLogFile(final String tag, final String message, final Throwable throwable) {
        String logContent = createLogContent(tag, message, throwable);
        LoggerUtils.writeLogContent(logContent);
    }

    private String createLogContent(String tag, String message, Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault());
        sb.append("\n")
                .append(simpleDateFormat.format(new Date()))
                .append(" || ")
                .append(tag)
                .append(" || ")
                .append(message);
        if (throwable != null) {
            sb.append(" || ").append(Log.getStackTraceString(throwable));
        }
        return sb.toString();
    }

    private void v(String tag, String message, Throwable t) {
        if (t != null) {
            Log.v(tag, message, t);
        } else {
            Log.v(tag, message);
        }
    }

    private void d(String tag, String message, Throwable t) {
        if (t != null) {
            Log.d(tag, message, t);
        } else {
            Log.d(tag, message);
        }
    }

    private void i(String tag, String message, Throwable t) {
        if (t != null) {
            Log.i(tag, message, t);
        } else {
            Log.i(tag, message);
        }
    }

    private void w(String tag, String message, Throwable t) {
        if (t != null) {
            Log.w(tag, message, t);
        } else {
            Log.w(tag, message);
        }
    }

    private void e(String tag, final String message, Throwable t) {
        if (t != null) {
            Log.e(tag, message, t);
        } else {
            Log.e(tag, message);
        }
    }
}