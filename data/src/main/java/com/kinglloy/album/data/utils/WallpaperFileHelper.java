package com.kinglloy.album.data.utils;

import android.content.Context;
import android.text.TextUtils;


import java.io.File;
import java.util.Set;

/**
 * YaLin On 2017/1/2.
 */

public class WallpaperFileHelper {

    public static final String ADVANCE_WALLPAPER_FOLDER = "component";

    public static void deleteOldComponent(Context context, Set<String> excludeNames) {
        File dir = getAdvanceWallpaperDir(context);
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

    public static File getAdvanceWallpaperDir(Context context) {
        return new File(context.getFilesDir(), ADVANCE_WALLPAPER_FOLDER);
    }

    public static boolean isNeedDownloadAdvanceComponent(boolean lazy, String storePath) {
        return !new File(storePath).exists();
    }
}
