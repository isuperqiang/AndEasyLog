package com.easy.log.test;

import android.app.Activity;
import android.os.Bundle;

import com.richie.easylog.ILogger;
import com.richie.easylog.LoggerFactory;

/**
 * @author richie
 *         测试
 */
public class MainActivity extends Activity {
    private final ILogger logger = LoggerFactory.getLogger(MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logger.verbose("onCreate()");

        logger.info("********************************");

        logger.json("{\"上海\":[\"浦东\"],\"四川\":[\"成都\",\"攀枝花\"],\"福建\":[\"福州\",\"厦门\",\"泉州\"]}");
        logger.info("********************************");

        logger.xml("<?xml version=\"1.0\"?><note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>");
        logger.info("********************************");

        logger.debug("打印多个参数。String:{}, int:{}, float:{}, long:{}, boolean:{}, char:{} etc.", "Log", 1, 10.24F, 1024L, false, 'c');
        logger.info("********************************");

        logger.warn(new NullPointerException("NPE"));
        logger.info("********************************");

        logger.debug("打印一段消息");

    }
}
