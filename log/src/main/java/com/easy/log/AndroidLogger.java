package com.easy.log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Android 日志打印
 */
class AndroidLogger implements ILogger {
    /* 日志级别 */
    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;

    /* Json字符 缩进距离 */
    private static final int JSON_INDENT = 2;
    /* 当前日志构建时间 */
    private static final long BEGINNING_TIME = System.nanoTime();
    /* 参数占位符 */
    private static final String PARAMS_PLACEHOLDER = "{}";
    /* 错误日志格式 */
    private static final String ERROR_LOG_FORMAT = "Error log format";
    /* 单次打印最大长度 */
    private static final int MAX_LOG_LENGTH = 4000;

    private final String tag;

    AndroidLogger(String tag) {
        this.tag = tag;
    }

    @Override
    public final void verbose(String message, Object... params) {
        log(VERBOSE, tag, message, null, params);
    }

    @Override
    public void debug(String message, Object... params) {
        log(DEBUG, tag, message, null, params);
    }

    @Override
    public void info(String message, Object... params) {
        log(INFO, tag, message, null, params);
    }

    @Override
    public void warn(Throwable throwable) {
        warn(null, throwable);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        log(WARN, tag, message, throwable);
    }

    @Override
    public void warn(String message, Object... params) {
        log(WARN, tag, message, null, params);
    }

    @Override
    public final void error(Throwable throwable) {
        error(null, throwable);
    }

    @Override
    public final void error(String message, Throwable throwable) {
        log(ERROR, tag, message, throwable);
    }

    @Override
    public final void error(String message, Object... params) {
        log(ERROR, tag, message, null, params);
    }

    @Override
    public void json(String json) {
        if (LogUtils.isEmpty(json)) {
            debug("Empty/Null json content");
            return;
        }
        try {
            json = json.trim();
            String message = "";
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                message = "JsonObject length:".concat(String.valueOf(jsonObject.length())).concat("\n").concat(jsonObject.toString(JSON_INDENT));
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                message = "JsonArray length:".concat(String.valueOf(jsonArray.length())).concat("\n").concat(jsonArray.toString(JSON_INDENT));
            }
            debug(message);
        } catch (Throwable e) {
            warn("Invalid Json", e);
        }
    }

    @Override
    public void xml(String xml) {
        if (LogUtils.isEmpty(xml)) {
            debug("Empty/Null xml content");
            return;
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            debug("Show xml:\n".concat(xmlOutput.getWriter().toString().replaceFirst(">", ">\n")));
        } catch (Throwable e) {
            warn("Invalid xml", e);
        }
    }

    private void log(int level, String tag, String message, Throwable throwable, Object... params) {
        String header = createHeader();
        String body = parseMessage(message, params);
        processLog(level, tag, header, body, throwable);
    }

    private String createHeader() {
        long usedTime = (System.nanoTime() - BEGINNING_TIME) / 1000000;
        StringBuilder header = new StringBuilder("[Time:");
        header.append(usedTime);
        header.append("][ThreadId:");
        header.append(Thread.currentThread().getId());
        header.append("][Line:");
        header.append(getLineNumber());
        header.append("] ");
        return header.toString();
    }

    private String parseMessage(final String message, final Object[] params) {
        StringBuilder body = new StringBuilder();
        if (message == null) {
            if (params.length != 0) {
                return ERROR_LOG_FORMAT;
            } else {
                return "";
            }
        }
        try {
            int index = 0;
            for (Object param : params) {
                int j = message.indexOf(PARAMS_PLACEHOLDER, index);
                if (j != -1) {
                    body.append(message.substring(index, j));
                    body.append(param);
                    index = j + 2;
                } else {
                    break;
                }
            }
            body.append(message.substring(index, message.length()));
            return body.toString();
        } catch (Throwable e) {
            warn(e);
            return ERROR_LOG_FORMAT;
        }
    }

    private int getLineNumber() {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            boolean find = false;
            for (StackTraceElement stackTraceElement : stackTrace) {
                if (stackTraceElement.getClassName().equals(AndroidLogger.class.getName())) {
                    find = true;
                    continue;
                }
                if (find) {
                    if (!stackTraceElement.getClassName().equals(AndroidLogger.class.getName())) {
                        return stackTraceElement.getLineNumber();
                    }
                }
            }
        } catch (Throwable e) {
            // ignored
        }
        return 0;
    }

    private void processLog(int level, String tag, String head, String body, Throwable throwable) {
        int length = body.length();
        if (length < MAX_LOG_LENGTH) {
            printAndroidLog(level, tag, head.concat(body), throwable);
        } else {
            int index = 0;
            int count = 0;
            String subBody;
            while (index < length) {
                count++;
                if (length <= index + MAX_LOG_LENGTH) {
                    subBody = body.substring(index);
                } else {
                    subBody = body.substring(index, index + MAX_LOG_LENGTH);
                }
                index += MAX_LOG_LENGTH;
                printAndroidLog(level, tag, head.concat("********(").concat(String.valueOf(count)).concat(")********").concat(subBody), throwable);
            }
        }
    }

    private void printAndroidLog(int level, String tag, String message, Throwable throwable) {
        if (LogUtils.isEmpty(message)) {
            message = "Empty/NULL log message";
        }
        if (level == VERBOSE) {
            v(tag, message, throwable);
        } else if (level == DEBUG) {
            d(tag, message, throwable);
        } else if (level == INFO) {
            i(tag, message, throwable);
        } else if (level == WARN) {
            w(tag, message, throwable);
        } else if (level == ERROR) {
            e(tag, message, throwable);
        }
    }

    private void v(String tag, String message, Throwable t) {
        if (t != null) {
            android.util.Log.v(tag, message, t);
        } else {
            android.util.Log.v(tag, message);
        }
    }

    private void d(String tag, String message, Throwable t) {
        if (t != null) {
            android.util.Log.d(tag, message, t);
        } else {
            android.util.Log.d(tag, message);
        }
    }

    private void i(String tag, String message, Throwable t) {
        if (t != null) {
            android.util.Log.i(tag, message, t);
        } else {
            android.util.Log.i(tag, message);
        }
    }

    private void w(String tag, String message, Throwable t) {
        if (t != null) {
            android.util.Log.w(tag, message, t);
        } else {
            android.util.Log.w(tag, message);
        }
    }

    private void e(String tag, final String message, Throwable t) {
        if (t != null) {
            android.util.Log.e(tag, message, t);
        } else {
            android.util.Log.e(tag, message);
        }
    }
}