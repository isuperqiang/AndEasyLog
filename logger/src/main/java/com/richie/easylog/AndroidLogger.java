package com.richie.easylog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Richie
 * Android 日志打印
 */
class AndroidLogger implements ILogger {

    /**
     * Json 字符 缩进距离
     */
    private static final int JSON_INDENT = 2;
    /**
     * 当前日志构建时间
     */
    private static final long BEGINNING_TIME = System.nanoTime();
    /**
     * 参数占位符
     */
    private static final String PARAMS_PLACEHOLDER = "{}";
    /**
     * 单次打印最大长度
     */
    private static final int MAX_LOG_LENGTH = 4000;
    /**
     * 纳秒转换成毫秒的倍数
     */
    private static final int TIME_CONVERT_UNIT = 1000000;
    /**
     * 分割线
     */
    private static final String SEPARATOR = " || ";
    /**
     * 日志的保存的类型
     */
    private static final String LOG_FILE_TYPE = ".log";
    /**
     * 日志内容的时间格式化，添加到每条日志的前面
     */
    private static final DateFormat LOG_FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS",
            Locale.getDefault());
    /**
     * 日志文件的名称，应用启动期间，全局使用一个
     */
    private static final String LOG_FILE_NAME = new SimpleDateFormat("yyyyMMdd_HHmmss",
            Locale.getDefault()).format(new Date()) + LOG_FILE_TYPE;
    /**
     * 写文件的线程池
     */
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    private final String tag;

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
            } else if (json.startsWith("[")) {
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
        StringBuilder sb = new StringBuilder();
        if (message == null) {
            if (params.length != 0) {
                return "Log format error";
            } else {
                return "";
            }
        }
        try {
            int index = 0;
            for (Object param : params) {
                int j = message.indexOf(PARAMS_PLACEHOLDER, index);
                if (j != -1) {
                    sb.append(message.substring(index, j));
                    sb.append(param);
                    index = j + 2;
                } else {
                    break;
                }
            }
            sb.append(message.substring(index, message.length()));
            return sb.toString();
        } catch (Throwable e) {
            warn(e);
            return "";
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
            printLog(level, tag, head + body, throwable);
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
                printLog(level, tag, head.concat("********(").concat(String.valueOf(count))
                        .concat(")********").concat(subBody), throwable);
            }
        }
    }

    private void printLog(int level, String tag, String message, Throwable throwable) {
        if (LogUtils.isEmpty(message)) {
            message = "Empty/Null log message";
        }
        if (LogConfig.isLogcatEnabled()) {
            printLogcat(level, tag, message, throwable);
        }
        if (LogConfig.isLogFileEnabled() && !LogUtils.isEmpty(LogConfig.getLogFileDir())) {
            printLogFile(tag, message, throwable);
        }
    }

    private void printLogcat(int level, String tag, String message, Throwable throwable) {
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
    }

    private void printLogFile(final String tag, final String message, final Throwable throwable) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                String logContent = createLogContent(tag, message, throwable);
                File file = createLogFile();
                if (file != null) {
                    write2File(file, logContent);
                }
            }
        });
    }

    private String createLogContent(String tag, String message, Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n")
                .append(LOG_FILE_DATE_FORMAT.format(new Date()))
                .append(SEPARATOR)
                .append(tag)
                .append(SEPARATOR)
                .append(message);
        if (throwable != null) {
            sb.append(SEPARATOR).append(android.util.Log.getStackTraceString(throwable));
        }
        return sb.toString();
    }

    private File createLogFile() {
        File logDir = new File(LogConfig.getLogFileDir());
        if (!logDir.exists()) {
            boolean mkdirs = logDir.mkdirs();
            if (!mkdirs) {
                return null;
            }
        }

        File logFile = new File(logDir, LOG_FILE_NAME);
        if (!logFile.exists()) {
            try {
                return logFile.createNewFile() ? logFile : null;
            } catch (IOException e) {
                if (LogConfig.isLogcatEnabled()) {
                    e.printStackTrace();
                }
                return null;
            }
        } else {
            return logFile;
        }
    }

    private void write2File(final File logFile, final String content) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            bufferedWriter.write(content);
        } catch (IOException e) {
            if (LogConfig.isLogcatEnabled()) {
                e.printStackTrace();
            }
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    if (LogConfig.isLogcatEnabled()) {
                        e.printStackTrace();
                    }
                }
            }
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