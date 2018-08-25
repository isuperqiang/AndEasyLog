package com.easy.log.test;

import android.app.Activity;
import android.os.Bundle;

import com.richie.easylog.ILogger;
import com.richie.easylog.LoggerFactory;

/**
 * @author Richie
 * 测试
 */
public class MainActivity extends Activity {
    private final ILogger logger = LoggerFactory.getLogger(MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        printTestLog();

        for (int i = 0; i < 10; i++) {
            ThreadHelper.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    printTestLog();
                }
            });
        }
    }

    private void printTestLog() {
        logger.info("********************************");
        logger.debug("打印一段消息");
        logger.info("********************************");
        logger.debug("打印多个参数。String:{}, int:{}, float:{}, long:{}, boolean:{}, char:{} etc.", "Log", 1, 10.24F, 1024L, false, 'c');
        logger.info("********************************");
        logger.json("{\"上海\":[\"浦东\"],\"四川\":[\"成都\",\"攀枝花\"],\"福建\":[\"福州\",\"厦门\",\"泉州\"]}");
        logger.info("********************************");
        logger.xml("<?xml version=\"1.0\"?><note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>");
        logger.info("********************************");
        logger.warn(new NullPointerException("NPE example"));
    }
}
