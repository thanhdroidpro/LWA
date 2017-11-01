package com.kinglloy.album.data.utils;

import android.content.Context;
import android.text.TextUtils;


import java.io.File;
import java.util.Set;

/**
 * YaLin On 2017/1/2.
 */

public class WallpaperFileHelper {

    public static final String LIVE_WALLPAPER_FOLDER = "component";
    public static final String STYLE_WALLPAPER_FOLDER = "style_wallpaper";

    public static void deleteOldLiveComponent(Context context, Set<String> excludeNames) {
        File dir = getLiveWallpaperDir(context);
        deleteFiles(context, dir, excludeNames);
    }

    public static void deleteOldStyleWallpaper(Context context, Set<String> excludeNames) {
        File dir = getStyleWallpaperDir(context);
        deleteFiles(context, dir, excludeNames);
    }

    private static void deleteFiles(Context context, File dir, Set<String> excludeNames) {
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles(fileName ->
                !excludeNames.contains(fileName.getName()));
        for (File file : files) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            NativeFileHelperKt.clearNativeFiles(context, file.getAbsolutePath());
        }
    }

    public static boolean ensureChecksumValid(Context context,
                                              String checksum, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        String computedChecksum = ChecksumUtil.getChecksum(file);
        if (TextUtils.equals(checksum, computedChecksum)) {
            return true;
        }
        //noinspection ResultOfMethodCallIgnored
        file.delete();
        return false;
    }

    public static File getLiveWallpaperDir(Context context) {
        return new File(context.getFilesDir(), LIVE_WALLPAPER_FOLDER);
    }

    public static File getStyleWallpaperDir(Context context) {
        return new File(context.getFilesDir(), STYLE_WALLPAPER_FOLDER);
    }

    public static boolean isNeedDownloadLiveComponent(boolean lazy, String storePath) {
        return !new File(storePath).exists();
    }
}
