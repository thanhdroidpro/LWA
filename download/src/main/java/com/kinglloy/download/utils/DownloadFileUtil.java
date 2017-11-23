package com.kinglloy.download.utils;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;


import com.kinglloy.download.KinglloyDownloader;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * @author jinyalin
 * @since 2017/5/27.
 */

public class DownloadFileUtil {

    private static int MIN_PROGRESS_STEP = 65536;
    private static long MIN_PROGRESS_TIME = 2000;

    private static String DEFAULT_SAVE_ROOT_PATH;

    public static String getDefaultSaveRootPath() {
        if (!TextUtils.isEmpty(DEFAULT_SAVE_ROOT_PATH)) {
            return DEFAULT_SAVE_ROOT_PATH;
        }

        if (KinglloyDownloader.getContext().getExternalCacheDir() == null) {
            return Environment.getDownloadCacheDirectory().getAbsolutePath();
        } else {
            // /storage/emulated/0/Android/data/com.baidu.appmanager.demo/cache
            //noinspection ConstantConditions
            return KinglloyDownloader.getContext().getExternalCacheDir().getAbsolutePath();
        }
    }

    public static void setDefaultSaveRootPath(final String path) {
        DEFAULT_SAVE_ROOT_PATH = path;
    }

    public static String getDefaultSaveFilePath(final String url) {
        return generateFilePath(getDefaultSaveRootPath(), generateFileName(url));
    }

    public static String generateFileName(final String url) {
        return md5(url);
    }

    public static String generateFilePath(String directory, String filename) {
        if (filename == null) {
            throw new IllegalStateException("can't generate real path, the file name is null");
        }

        if (directory == null) {
            throw new IllegalStateException("can't generate real path, the directory is null");
        }

        return formatString("%s%s%s", directory, File.separator, filename);
    }

    public static String formatString(final String msg, Object... args) {
        return String.format(Locale.ENGLISH, msg, args);
    }

    private static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * The same to {@link File#getParent()}, for non-creating a file object.
     *
     * @return this file's parent pathname or {@code null}.
     */
    public static String getParent(final String path) {
        int length = path.length(), firstInPath = 0;
        if (File.separatorChar == '\\' && length > 2 && path.charAt(1) == ':') {
            firstInPath = 2;
        }
        int index = path.lastIndexOf(File.separatorChar);
        if (index == -1 && firstInPath > 0) {
            index = 2;
        }
        if (index == -1 || path.charAt(length - 1) == File.separatorChar) {
            return null;
        }
        if (path.indexOf(File.separatorChar) == index
                && path.charAt(firstInPath) == File.separatorChar) {
            return path.substring(0, index + 1);
        }
        return path.substring(0, index);
    }

    private final static String DOWNLOADER_PREFIX = "FileDownloader";

    public static String getThreadPoolName(String name) {
        return DOWNLOADER_PREFIX + "-" + name;
    }

    /**
     * @param targetPath The target path for the download task.
     * @return The temp path is {@code targetPath} in downloading status; The temp path is used for
     * storing the file not completed downloaded yet.
     */
    public static String getTempPath(final String targetPath) {
        return String.format(Locale.ENGLISH, "%s.temp", targetPath);
    }

    public static long getFreeSpaceBytes(final String path) {
        long freeSpaceBytes;
        final StatFs statFs = new StatFs(path);
        if (Build.VERSION.SDK_INT >= 18) {
            freeSpaceBytes = statFs.getAvailableBytes();
        } else {
            //noinspection deprecation
            freeSpaceBytes = statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
        }

        return freeSpaceBytes;
    }

    public static int getMinProgressStep() {
        return MIN_PROGRESS_STEP;
    }

    public static long getMinProgressTime() {
        return MIN_PROGRESS_TIME;
    }

}
