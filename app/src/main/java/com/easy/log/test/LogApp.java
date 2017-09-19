package com.easy.log.test;

import android.app.Application;

import com.richie.easylog.ILogger;
import com.richie.easylog.LoggerFactory;

/**
 * @author Richie on 2017.09.19
 */
public class LogApp extends Application {
    static {
        /* 关闭日志打印，默认开启 */
        // LoggerFactory.setLogEnabled(false);
    }

    private final ILogger logger = LoggerFactory.getLogger(LogApp.class);

    @Override
    public void onCreate() {
        super.onCreate();
        logger.debug("LogApp onCreate");
    }
}
