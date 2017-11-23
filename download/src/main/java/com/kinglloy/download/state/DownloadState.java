package com.kinglloy.download.state;

/**
 * @author jinyalin
 * @since 2017/5/31.
 */

public interface DownloadState {
    /**
     * 未知状态，也许request不存在
     */
    int STATE_UNKNOWN = -1;
    /**
     * 等待下载
     */
    int STATE_PENDING = 1;
    /**
     * 下载中
     */
    int STATE_DOWNLOADING = 2;
    /**
     * 下载暂停
     */
    int STATE_PAUSE = 3;
    /**
     * 下载完成
     */
    int STATE_COMPLETE = 4;
    /**
     * 下载被取消
     */
    int STATE_CANCEL = 5;

    /**
     * 下载出错，可以从mErrorMsg中读到错误原因
     */
    int STATE_ERROR = 6;
}
