package com.kinglloy.download.utils;

/**
 * 下载的通用工具类
 *
 * @author jinyalin
 * @since 2017/5/31.
 */
public class DownloadUtil {
    public static long convertContentLengthString(String s) {
        if (s == null) {
            return -1;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
