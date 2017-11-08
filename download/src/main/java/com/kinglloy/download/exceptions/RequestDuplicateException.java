package com.kinglloy.download.exceptions;

/**
 * @author jinyalin
 * @since 2017/5/28.
 */

public class RequestDuplicateException extends Exception {
    private long downloadId;

    public RequestDuplicateException(long downloadId, String message) {
        super(message);
        this.downloadId = downloadId;
    }

    public long getDownloadId() {
        return downloadId;
    }
}
