package com.kinglloy.download.module;

import com.kinglloy.download.KinglloyDownloader.Request;
import com.kinglloy.download.state.DownloadState;

/**
 * 记录下载大小和下载状态的Request
 *
 * @author jinyalin
 * @since 2017/5/28.
 */
public class InternalRequest extends Request {
    private long mDownloadedSize;
    private long mTotalSize;

    private int mDownloadState = DownloadState.STATE_PENDING;
    private String mErrorMsg;
    private String mExtra;

    public InternalRequest(String uriString) {
        super(uriString);
    }

    public InternalRequest(Request origin) {
        super(origin);
        if (origin instanceof InternalRequest) {
            InternalRequest internalRequest = (InternalRequest) origin;
            mDownloadedSize = internalRequest.getDownloadedSize();
            mTotalSize = internalRequest.getTotalSize();
            mDownloadState = internalRequest.getDownloadState();
            mErrorMsg = internalRequest.getErrorMsg();
            mExtra = internalRequest.getExtra();
        }
    }

    public long getDownloadedSize() {
        return mDownloadedSize;
    }

    public InternalRequest setDownloadedSize(long downloadedSize) {
        this.mDownloadedSize = downloadedSize;
        return this;
    }

    public long getTotalSize() {
        return mTotalSize;
    }

    public InternalRequest setTotalSize(long totalSize) {
        this.mTotalSize = totalSize;
        return this;
    }

    public int getDownloadState() {
        return mDownloadState;
    }

    public InternalRequest setDownloadState(int downloadState) {
        this.mDownloadState = downloadState;
        return this;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public InternalRequest setErrorMsg(String errorMsg) {
        this.mErrorMsg = errorMsg;
        return this;
    }

    public String getExtra() {
        return mExtra;
    }

    public InternalRequest setExtra(String extra) {
        this.mExtra = extra;
        return this;
    }
}
