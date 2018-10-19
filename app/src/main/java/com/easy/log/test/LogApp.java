package com.easy.log.test;

import android.app.Application;

import com.richie.easylog.LoggerConfig;

/**
 * @author Richie on 2017.09.19
 */
public class LogApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /*打开 logcat 日志*/
        LoggerConfig.setLogcatEnabled(true);
        /*打开 文件 日志*/
        LoggerConfig.setLogFileEnabled(true);
    }
}
