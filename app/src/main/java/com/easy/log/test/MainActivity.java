package com.easy.log.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.richie.easylog.ILogger;
import com.richie.easylog.LoggerFactory;

/**
 * @author Richie on 2018.01.10
 */
public class MainActivity extends AppCompatActivity {
    private final ILogger logger = LoggerFactory.getLogger(MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        printTestLog();
    }

    private void printTestLog() {
        logger.info("********************************");
        logger.debug("打印一段消息");
        logger.info("********************************");
        int[] intArray = {1, 2, 3};
        logger.debug("打印数组：{}", intArray);
        logger.info("********************************");
        logger.debug("打印多个参数。String:{}, int:{}, float:{}, long:{}, boolean:{}, char:{} etc.", "Log", 1, 10.24F, 1024L, false, 'c');
        logger.info("********************************");
        Bundle bundle = new Bundle();
        bundle.putString("a", "12");
        bundle.putInt("b", 3);
        bundle.putDouble("c", 9.9);
        logger.debug("bundle:{}", bundle);
        logger.info("********************************");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("data", bundle);
        logger.debug("intent:{}", intent);
        logger.info("********************************");
        String json = logger.json("{\"上海\":[\"浦东\"],\"四川\":[\"成都\",\"攀枝花\"],\"福建\":[\"福州\",\"厦门\",\"泉州\"]}");
        logger.debug(json);
        logger.info("********************************");
        String xml = logger.xml("<?xml version=\"1.0\"?><note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>");
        logger.debug(xml);
        logger.info("********************************");
        NullPointerException npe = new NullPointerException("NPE example");
        logger.warn(npe);
        String npeStackTrace = logger.stackTrack(npe);
        logger.error("NPE StackTrace:{}", npeStackTrace);

        logger.info("********************************");
        String haiyan = "在苍茫的大海上，狂风卷集着乌云。在乌云和大海之间，海燕像黑色的闪电，在高傲的飞翔。一会儿翅膀碰着波浪，" +
                "一会儿箭一般地直冲向乌云，它叫喊着，——就在这鸟儿勇敢的叫喊声里，乌云听出了欢乐。在这叫喊声里——充满着对暴风雨的渴望！" +
                "在这叫喊声里，乌云听出了愤怒的力量、热情的火焰和胜利的信心。海鸥在暴风雨来临之前呻吟着，——呻吟着，它们在大海上飞窜，" +
                "想把自己对暴风雨的恐惧，掩藏到大海深处。海鸭也在呻吟着，——它们这些海鸭啊，享受不了生活的战斗的欢乐：" +
                "轰隆隆的雷声就把它们吓坏了。蠢笨的企鹅，胆怯地把肥胖的身体躲藏到悬崖底下……只有那高傲的海燕，勇敢地，自由自在的，" +
                "在泛起白沫的大海上飞翔！乌云越来越暗，越来越低，向海面直压下来，而波浪一边歌唱，一边冲向高空，去迎接那雷声。雷声轰响。" +
                "波浪在愤怒的飞沫中呼叫，跟狂风争鸣。看吧，狂风紧紧抱起一层层巨浪，恶狠狠地把它们甩到悬崖上，把这些大块的翡翠摔成尘雾和碎末。" +
                "海燕叫喊着，飞翔着，像黑色的闪电，箭一般地穿过乌云，翅膀掠起波浪的飞沫。看吧，它飞舞着，像个精灵，——高傲的、黑色的暴风雨的精灵，" +
                "——它在大笑，它又在号叫……它笑些乌云，它因为欢乐而号叫！这个敏感的精灵，——它从雷声的震怒里，早就听出了困乏，它深信，乌云遮不住太阳，" +
                "——是的，遮不住的！狂风吼叫，雷声轰响……一堆堆乌云，像青色的火焰，在无底在大海上燃烧。大海抓住闪电的箭光，把它们熄灭在自己的深渊里。" +
                "这些闪电的影子，活像一条条火蛇，在大海里蜿蜒游动，一晃就消失了。——暴风雨！暴风雨就要来啦！这是勇敢的海燕，在怒吼的大海上，在闪电中间，" +
                "高傲的飞翔；这是胜利的预言家在叫喊：——让暴风雨来得更猛烈些吧！";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(haiyan);
        }
        logger.debug("{}", sb);
    }
}
