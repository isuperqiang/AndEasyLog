package com.richie.easylog;

import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * 日志工具类
 * Logger utils
 *
 * @author Richie
 */
final class LoggerUtils {

    static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    static String array2String(Object object) {
        if (object == null) {
            return "null";
        }
        if (!object.getClass().isArray()) {
            return object.toString();
        }
        if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        }
        if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        }
        if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        }
        if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        }
        if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        }
        if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        }
        if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        }
        if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        }
        if (object instanceof Object[]) {
            return Arrays.deepToString((Object[]) object);
        }
        return "Couldn't find a correct type for the object";
    }

    static String intent2String(Intent intent) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Intent {");
        boolean first = true;
        String action = intent.getAction();
        if (action != null) {
            sb.append("act=").append(action);
            first = false;
        }
        Set<String> categories = intent.getCategories();
        if (categories != null) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("cat=[");
            boolean firstCategory = true;
            for (String c : categories) {
                if (!firstCategory) {
                    sb.append(',');
                }
                sb.append(c);
                firstCategory = false;
            }
            sb.append("]");
        }
        Uri data = intent.getData();
        if (data != null) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("dat=").append(data);
        }
        String mType = intent.getType();
        if (mType != null) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("typ=").append(mType);
        }
        int flags = intent.getFlags();
        if (flags != 0) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("flg=0x").append(Integer.toHexString(flags));
        }
        String aPackage = intent.getPackage();
        if (aPackage != null) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("pkg=").append(aPackage);
        }
        ComponentName component = intent.getComponent();
        if (component != null) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("cmp=").append(component.flattenToShortString());
        }
        Rect sourceBounds = intent.getSourceBounds();
        if (sourceBounds != null) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("bnds=").append(sourceBounds.toShortString());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData mClipData = intent.getClipData();
            if (mClipData != null) {
                if (!first) {
                    sb.append(' ');
                }
                first = false;
                clipData2String(mClipData, sb);
            }
        }
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (!first) {
                sb.append(' ');
            }
            first = false;
            sb.append("extras={");
            sb.append(bundle2String(extras));
            sb.append('}');
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Intent mSelector = intent.getSelector();
            if (mSelector != null) {
                if (!first) {
                    sb.append(' ');
                }
                sb.append("sel={");
                sb.append(mSelector == intent ? "(this Intent)" : intent2String(mSelector));
                sb.append("}");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    static String bundle2String(Bundle bundle) {
        Iterator<String> iterator = bundle.keySet().iterator();
        if (!iterator.hasNext()) {
            return "Bundle {}";
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("Bundle {");
        for (; ; ) {
            String key = iterator.next();
            Object value = bundle.get(key);
            sb.append(key).append('=');
            if (value instanceof Bundle) {
                sb.append(value == bundle ? "(this Bundle)" : bundle2String((Bundle) value));
            } else {
                sb.append(value);
            }
            if (!iterator.hasNext()) {
                return sb.append("}").toString();
            }
            sb.append(',').append(' ');
        }
    }

    private static void clipData2String(ClipData clipData, StringBuilder sb) {
        ClipData.Item item = clipData.getItemAt(0);
        if (item == null) {
            sb.append("ClipData.Item {}");
            return;
        }
        sb.append("ClipData.Item { ");
        String mHtmlText = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mHtmlText = item.getHtmlText();
        }
        if (mHtmlText != null) {
            sb.append("H:");
            sb.append(mHtmlText);
            sb.append("}");
            return;
        }
        CharSequence text = item.getText();
        if (text != null) {
            sb.append("T:");
            sb.append(text);
            sb.append("}");
            return;
        }
        Uri uri = item.getUri();
        if (uri != null) {
            sb.append("U:").append(uri);
            sb.append("}");
            return;
        }
        Intent intent = item.getIntent();
        if (intent != null) {
            sb.append("I:");
            sb.append(intent2String(intent));
            sb.append("}");
            return;
        }
        sb.append("NULL");
        sb.append("}");
    }

    static File getLogFileDir(Context context) {
        File cacheDir = getCacheDir(context);
        File logDir = new File(cacheDir, "logger");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        return logDir;
    }

    private static File getCacheDir(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    static void writeText2File(final File logFile, final String content) throws IOException {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            bufferedWriter.write(content);
            bufferedWriter.flush();
        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        }
    }

    static boolean checkDiskSize(Context context, long maxFolderSize) {
        File logFileDir = getLogFileDir(context);
        long folderSize = getFolderSize(logFileDir);
        if (folderSize >= maxFolderSize) {
            deleteFileRecursive(logFileDir);
            return true;
        }
        return false;
    }

    private static long getFolderSize(File folder) {
        File[] files = folder.listFiles();
        if (files == null) {
            return 0;
        }
        long length = 0;
        for (File file : files) {
            if (file.isFile()) {
                length += file.length();
            } else {
                length += getFolderSize(file);
            }
        }
        return length;
    }

    private static void deleteFileRecursive(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        deleteFileRecursive(f);
                    }
                }
            } else {
                file.delete();
            }
        }
    }

    static String getDeviceInfo() {
        String versionName = "";
        int versionCode = 0;
        try {
            Context context = LoggerFactory.getLoggerConfig().getContext();
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
        } catch (Exception e) {
            if (LoggerFactory.getLoggerConfig().isLogcatEnabled()) {
                Log.e("LoggerUtils", "", e);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("************* Log Head ****************");
        sb.append("\nDevice Manufacturer: ").append(Build.MANUFACTURER)
                .append("\nDevice Model       : ").append(Build.MODEL)
                .append("\nAndroid Version    : ").append(Build.VERSION.RELEASE)
                .append("\nAndroid SDK        : ").append(Build.VERSION.SDK_INT)
                .append("\nApp VersionName    : ").append(versionName)
                .append("\nApp VersionCode    : ").append(versionCode)
                .append("\n************* Log Head ****************\n\n");
        return sb.toString();
    }
}
