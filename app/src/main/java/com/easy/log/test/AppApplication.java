package com.easy.log.test;

import android.app.Application;

import com.richie.easylog.LogConfig;

/**
 * @author Richie on 2017.05.21
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogConfig.setLogEnable(true);
    }
}
