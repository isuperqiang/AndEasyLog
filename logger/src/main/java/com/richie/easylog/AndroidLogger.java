package com.richie.easylog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

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
     * Json 字符缩进距离
     */
    private static final int JSON_INDENT = 2;
    /**
     * 参数占位符
     */
    private static final String PARAMS_PLACEHOLDER = "{}";
    /**
     * 单条打印最大长度
     */
    private static final int MESSAGE_MAX_LENGTH = 4000;
    /**
     * 添加到日志文件的分割线
     */
    private static final String SEPARATOR = " || ";
    /**
     * 日志保存后的文件类型
     */
    private static final String LOG_FILE_TYPE = ".log";
    /**
     * 日志内容的时间格式化，添加到每条日志的前面
     */
    private static final DateFormat LOG_FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS",
            Locale.getDefault());
    /**
     * 日志文件的名称，应用运行期间，保存到同一个文件
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
        if (LogUtils.isEmpty(json)) {
            debug("Empty/Null json content");
            return;
        }
        try {
            json = json.trim();
            StringBuilder sb = new StringBuilder();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                sb.append("JsonObject length:")
                        .append(jsonObject.length())
                        .append("\n")
                        .append(jsonObject.toString(JSON_INDENT));
            } else if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                sb.append("JsonArray length:")
                        .append(jsonArray.length())
                        .append("\n")
                        .append(jsonArray.toString(JSON_INDENT));
            }
            debug(sb.toString());
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
        String header = createLogHeader();
        String body = createLogBody(message, params);
        if (LogUtils.isEmpty(body)) {
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
                    sb.append(LogUtils.toString(param));
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
            if (LogConfig.isLogcatEnabled()) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private void processLog(int level, String tag, String head, String body, Throwable throwable) {
        int length = body.length();
        if (length < MESSAGE_MAX_LENGTH) {
            printLog(level, tag, head.concat(body), throwable);
        } else {
            int index = 0;
            int count = 0;
            String subBody;
            while (index < length) {
                count++;
                if (length <= index + MESSAGE_MAX_LENGTH) {
                    subBody = body.substring(index);
                } else {
                    subBody = body.substring(index, index + MESSAGE_MAX_LENGTH);
                }
                index += MESSAGE_MAX_LENGTH;
                printLog(level, tag, head.concat("********(").concat(String.valueOf(count))
                        .concat(")********").concat(subBody), throwable);
            }
        }
    }

    private void printLog(int level, String tag, String message, Throwable throwable) {
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
                    try {
                        writeContent2File(file, logContent);
                    } catch (IOException e) {
                        if (LogConfig.isLogcatEnabled()) {
                            e.printStackTrace();
                        }
                    }
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
            sb.append(SEPARATOR).append(LogUtils.getStackTraceString(throwable));
        }
        return sb.toString();
    }

    private String getDeviceInfo() {
        String versionName = "";
        int versionCode = 0;
        Context context = LoggerFactory.getAppContext();
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            if (LogConfig.isLogcatEnabled()) {
                e.printStackTrace();
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("************* Log Head ****************");
        sb.append("\nDevice Manufacturer: ").append(Build.MANUFACTURER)
                .append("\nDevice Model       : ").append(Build.MODEL)
                .append("\nAndroid Version    : ").append(Build.VERSION.RELEASE)
                .append("\nAndroid SDK        : ").append(Build.VERSION.SDK_INT)
                .append("\nApp VersionName    : ").append(versionName)
                .append("\nApp VersionCode    : ").append(versionCode)
                .append("\n************* Log Head ****************\n\n");
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
                boolean ret = logFile.createNewFile();
                if (ret) {
                    String deviceInfo = getDeviceInfo();
                    writeContent2File(logFile, deviceInfo);
                }
                return ret ? logFile : null;
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

    private void writeContent2File(final File logFile, final String content) throws IOException {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            bufferedWriter.write(content);
        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
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