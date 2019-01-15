package com.easy.log.test;

import android.app.Application;

import com.richie.easylog.LoggerConfig;
import com.richie.easylog.LoggerFactory;

/**
 * @author Richie on 2017.09.19
 */
public class LogApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /*打开 logcat 日志*/
        /*打开 文件 日志*/
        LoggerFactory.init(new LoggerConfig.Builder()
                .context(this)
                .logcatEnabled(true)
                .logFileEnabled(true)
                .build());
    }
}
