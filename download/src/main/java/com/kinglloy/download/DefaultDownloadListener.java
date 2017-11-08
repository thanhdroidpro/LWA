package com.kinglloy.download;

/**
 * a default impl for download listener
 *
 * @author jinyalin
 * @since 2017/5/31.
 */
public class DefaultDownloadListener implements DownloadListener {
    @Override
    public void onDownloadPending(long downloadId) {

    }

    @Override
    public void onDownloadProgress(long downloadId, long downloadedSize, long totalSize) {

    }

    @Override
    public void onDownloadPause(long downloadId) {

    }

    @Override
    public void onDownloadComplete(long downloadId, String path) {

    }

    @Override
    public void onDownloadError(long downloadId, int errorCode, String errorMessage) {

    }
}
