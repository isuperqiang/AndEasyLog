# AndEasyLog
[![](https://jitpack.io/v/isuperqiang/AndEasyLog.svg)](https://jitpack.io/#isuperqiang/AndEasyLog)

一款简洁实用的 Android 日志库

### 添加依赖：
第一步：在工程根目录 build.gradle 的 allprojects → repositories 下面添加 JitPack 仓库

```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

第二步：在模块 build.gradle 添加依赖

```
    dependencies {
        implementation 'com.github.isuperqiang:AndEasyLog:1.3.1'
    }
```

### 特点：
1. 支持 5 种日志打印级别：verbose、debug、info、warn、error
2. 支持格式化打印 Json 和 Xml
3. 支持打印代码执行时间、线程 ID 和行号
4. 支持指定 Tag，配合 AS 的 LiveTemplates 使用更酸爽
5. 支持打印无限长的日志，没有 4k 字符的限制
6. 支持输出到文件，配置保存目录即可

### 使用：
1. **开关配置**：

> 在程序入口（一般是 Application）的 onCreate 方法中，配置全局日志的开关。

```java
    /*打开 logcat 日志*/
    LogConfig.setLogcatEnabled(true);

    /*打开 文件 日志*/
    LogConfig.setLogFileConfig(true, dir);
```

2. **初始化**：

> 在每个类里面实例化 ILogger。

`private final ILogger logger = LoggerFactory.getLogger("MainActivity");`

或者

`private final ILogger logger = LoggerFactory.getLogger(MainActivity.class);`

3. **打印日志**：

* `logger.debug("打印一段消息");`
* `logger.debug("打印多个参数。String:{}, int:{}, long:{}, boolean:{}, char:{} etc.", "AndroidLog", 100, 1000L, false, 'c');`
* `logger.json("{\"上海\":[\"浦东\"],\"四川\":[\"成都\",\"攀枝花\"],\"福建\":[\"福州\",\"厦门\",\"泉州\"]}");`
* `logger.xml("<?xml version=\"1.0\"?><note><to>Tove</to><from>Jani</from><heading>Reminder</heading><body>Don't forget me this weekend!</body></note>");`
* `logger.warn(new NullPointerException("NPE"));`

  <img src='images/log_snapshot.jpg'/>

4. **消息说明**：

>  time: 从日志创建起经过的毫秒时间值；tid: 线程 ID；line: 行号。

5. **混淆规则**：

>  没有限制，随便混淆。

## 关于我
* [博客](https://isuperqiang.cn)
* [简书](http://www.jianshu.com/u/d5f18207fa2e)

## 许可
<pre>
Copyright 2017 Richie Liu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
