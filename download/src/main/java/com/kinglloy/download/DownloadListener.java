package com.kinglloy.download;

/**
 * a download state listener
 *
 * @author jinyalin
 * @since 2017/5/31.
 */
public interface DownloadListener {
    /**
     * When download request be enqueue to the queue, and prepare to download.
     *
     * @param downloadId target download id
     */
    void onDownloadPending(long downloadId);

    /**
     * When download request progress update,
     * the method will tell you how many progress you should show
     *
     * @param downloadedSize already download size
     * @param totalSize      total file size
     */
    void onDownloadProgress(long downloadId, long downloadedSize, long totalSize);

    /**
     * When download request be paused
     */
    void onDownloadPause(long downloadId);

    /**
     * When download request complete
     *
     * @param path the local path for the download request.
     *             if the destination path be set, the path is equals destination path.
     *             Else it is the default cache path.
     */
    void onDownloadComplete(long downloadId, String path);

    /**
     * When download have some error
     */
    void onDownloadError(long downloadId, int errorCode, String errorMessage);
}
