package com.kinglloy.download.exceptions;

/**
 * @author jinyalin
 * @since 2017/5/31.
 */

public class RequestNotExistException extends Exception {
    private long downloadId;


    public RequestNotExistException(long downloadId, String message) {
        super(message);
        this.downloadId = downloadId;
    }

    public long getDownloadId() {
        return downloadId;
    }
}
