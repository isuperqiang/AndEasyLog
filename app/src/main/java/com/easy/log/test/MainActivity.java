package com.easy.log.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.easy.log.ILogger;
import com.easy.log.LoggerFactory;

public class MainActivity extends AppCompatActivity {
    private final ILogger log = LoggerFactory.getLogger("MainActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        log.verbose("onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        log.info("********************************");

        log.json("{\"上海\":[\"浦东\"],\"四川\":[\"成都\",\"攀枝花\"],\"福建\":[\"福州\",\"厦门\",\"泉州\"]}");
        log.info("********************************");

        log.xml("<?xml version=\"1.0\"?><note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>");
        log.info("********************************");

        log.debug("多个参数的例子。String:{}, int:{}, long:{}, boolean:{}, char:{} etc.", "Log", 1, 1000L, false, 'c');
        log.info("********************************");

        log.warn(new NullPointerException("NPE"));
    }
}
