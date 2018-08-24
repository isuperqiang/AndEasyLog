package com.richie.easylog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Richie on 2018.01.10
 */
class FilePrinter {

    /**
     * 分割线
     */
    private static final String SEPARATOR = " || ";

    /**
     * 日志的保存的类型
     */
    private static final String SAVE_FILE_TYPE = ".log";

    /**
     * 文件的名称以日期命名
     */
    private final DateFormat mFileDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

    /**
     * 在每一条日志前面增加一个时间戳
     */
    private final DateFormat mLogDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS", Locale.getDefault());

    /**
     * 日志命名的一部分：时间戳
     */
    private final String mLogCreateTime = mFileDateFormat.format(new Date(System.currentTimeMillis()));

    /**
     * 日志全名拼接
     */
    private final String mLogFileName = mLogCreateTime + SAVE_FILE_TYPE;

    void printLog2File(final String tag, final String message, final Throwable throwable) {
        ThreadHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String logMsg = formatLogMsg(tag, message, throwable);
                synchronized (FilePrinter.class) {
                    File file = createFile();
                    if (file != null) {
                        writeText(file, logMsg);
                    }
                }
            }
        });
    }

    private String formatLogMsg(String tag, String message, Throwable throwable) {
        String timeStr = mLogDateFormat.format(Calendar.getInstance().getTime());
        StringBuilder sb = new StringBuilder();
        sb.append("\n")
                .append(timeStr)
                .append(SEPARATOR)
                .append(tag)
                .append(SEPARATOR)
                .append(message);
        if (throwable != null) {
            sb.append(SEPARATOR)
                    .append(android.util.Log.getStackTraceString(throwable));
        }
        return sb.toString();
    }

    private File createFile() {
        File logDir = new File(LogConfig.getLogPrintDir());
        if (!logDir.exists()) {
            boolean mkdirs = logDir.mkdirs();
            if (!mkdirs) {
                return null;
            }
        }

        File logFile = new File(logDir, mLogFileName);
        if (!logFile.exists()) {
            try {
                return logFile.createNewFile() ? logFile : null;
            } catch (IOException e) {
                if (LogConfig.isLogEnabled()) {
                    e.printStackTrace();
                }
                return null;
            }
        } else {
            return logFile;
        }
    }

    private void writeText(final File logFile, final String content) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(logFile, true);
            outputStream.write(content.getBytes("UTF-8"));
            outputStream.flush();
        } catch (Exception e) {
            if (LogConfig.isLogEnabled()) {
                e.printStackTrace();
            }
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    if (LogConfig.isLogEnabled()) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
