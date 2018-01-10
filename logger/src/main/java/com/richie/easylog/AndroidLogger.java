package com.richie.easylog;

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
 * @author richie
 *         Android 日志打印
 */
class AndroidLogger implements ILogger {

    /* Json 字符 缩进距离 */
    private static final int JSON_INDENT = 2;
    /* 当前日志构建时间 */
    private static final long BEGINNING_TIME = System.nanoTime();
    /* 参数占位符 */
    private static final String PARAMS_PLACEHOLDER = "{}";
    /* 错误日志格式 */
    private static final String ERROR_LOG_FORMAT = "Log format error";
    /* 单次打印最大长度 */
    private static final int MAX_LOG_LENGTH = 4000;
    /* 纳秒转换成毫秒的倍数 */
    private static final int TIME_CONVERT_UNIT = 1000000;

    private final String tag;

    private FilePrinter mFilePrinter;

    AndroidLogger(String tag) {
        this.tag = tag;
    }

    @Override
    public void verbose(String message, Object... params) {
        log(android.util.Log.VERBOSE, tag, message, null, params);
    }

    @Override
    public void debug(String message, Object... params) {
        log(android.util.Log.DEBUG, tag, message, null, params);
    }

    @Override
    public void info(String message, Object... params) {
        log(android.util.Log.INFO, tag, message, null, params);
    }

    @Override
    public void warn(Throwable throwable) {
        warn(null, throwable);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        log(android.util.Log.WARN, tag, message, throwable);
    }

    @Override
    public void warn(String message, Object... params) {
        log(android.util.Log.WARN, tag, message, null, params);
    }

    @Override
    public void error(Throwable throwable) {
        error(null, throwable);
    }

    @Override
    public void error(String message, Throwable throwable) {
        log(android.util.Log.ERROR, tag, message, throwable);
    }

    @Override
    public void error(String message, Object... params) {
        log(android.util.Log.ERROR, tag, message, null, params);
    }

    @Override
    public void json(String json) {
        if (LogUtils.isEmpty(json)) {
            debug("Empty/Null json content");
            return;
        }
        try {
            json = json.trim();
            StringBuilder message = new StringBuilder();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                message.append("JsonObject length:")
                        .append(jsonObject.length())
                        .append("\n")
                        .append(jsonObject.toString(JSON_INDENT));
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                message.append("JsonArray length:")
                        .append(jsonArray.length())
                        .append("\n")
                        .append(jsonArray.toString(JSON_INDENT));
            }
            debug(message.toString());
        } catch (Throwable e) {
            warn("Invalid json", e);
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
            debug("XML:\n".concat(xmlOutput.getWriter().toString().replaceFirst(">", ">\n")));
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
        long usedTime = (System.nanoTime() - BEGINNING_TIME) / TIME_CONVERT_UNIT;
        StringBuilder header = new StringBuilder("[time:");
        header.append(usedTime);
        header.append("][tid:");
        header.append(Thread.currentThread().getId());
        header.append("][line:");
        header.append(getLineNumber());
        header.append("] ");
        return header.toString();
    }

    private String parseMessage(String message, Object[] params) {
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
        } catch (Throwable e) {
            // ignored
        }
        return 0;
    }

    private void processLog(int level, String tag, String head, String body, Throwable throwable) {
        int length = body.length();
        if (length < MAX_LOG_LENGTH) {
            printAndroidLog(level, tag, head + body, throwable);
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
                printAndroidLog(level, tag, head.concat("********(").concat(String.valueOf(count))
                        .concat(")********").concat(subBody), throwable);
            }
        }
    }

    private void printAndroidLog(int level, String tag, String message, Throwable throwable) {
        if (LogUtils.isEmpty(message)) {
            message = "Empty/NULL log message";
        }
        switch (level) {
            case android.util.Log.VERBOSE:
                v(tag, message, throwable);
                break;
            case android.util.Log.DEBUG:
                d(tag, message, throwable);
                break;
            case android.util.Log.INFO:
                i(tag, message, throwable);
                break;
            case android.util.Log.WARN:
                w(tag, message, throwable);
                break;
            case android.util.Log.ERROR:
                e(tag, message, throwable);
                break;
            default:
                break;
        }

        if (LogConfig.isPrint2File()) {
            if (mFilePrinter == null) {
                mFilePrinter = new FilePrinter();
            }
            mFilePrinter.printLog2File(tag, message, throwable);
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