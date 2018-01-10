package com.richie.easylog;

/**
 * @author Richie on 2018.01.10
 *         日志配置
 */
public class LogConfig {
    /**
     * 日志开关
     */
    private static boolean sLogEnabled = true;
    /**
     * 打印到文件
     */
    private static boolean sPrint2File = false;
    /**
     * 日志文件保存路径
     */
    private static String sLogPrintDir = "/sdcard/AndEasyLog";

    public static boolean isLogEnabled() {
        return sLogEnabled;
    }

    public static void setLogEnabled(boolean logEnabled) {
        sLogEnabled = logEnabled;
    }

    public static boolean isPrint2File() {
        return sPrint2File;
    }

    public static void setPrint2File(boolean print2File) {
        sPrint2File = print2File;
    }

    public static void setPrintLogDir(String logPath) {
        sLogPrintDir = logPath;
    }

    public static String getLogPrintDir() {
        return sLogPrintDir;
    }
}
