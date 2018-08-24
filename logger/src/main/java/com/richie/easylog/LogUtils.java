package com.richie.easylog;

/**
 * @author Richie
 * 日志工具类
 */
class LogUtils {
    /**
     * Returns true if the string is null or 0-length.
     * Copied from android.text.TextUtils.isEmpty.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
