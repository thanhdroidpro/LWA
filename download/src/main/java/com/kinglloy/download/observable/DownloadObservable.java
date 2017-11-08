package com.kinglloy.download.observable;

import com.kinglloy.download.DownloadListener;
import com.kinglloy.download.schedule.IdRequest;

/**
 * download observable. When download event, the observable will
 * be invoke
 *
 * @author jinyalin
 * @since 2017/5/31.
 */
public interface DownloadObservable {

    /**
     * notify listeners the state changed
     */
    void notifyDownloadStateChange(IdRequest request, int newState);

    /**
     * notify listeners the progress update
     */
    void notifyDownloadProgress(IdRequest request, long downloadedSize, long totalSize);

    /**
     * notify listeners download has a error
     */
    void notifyError(IdRequest request, int errorCode, String errorMessage);

    /**
     * notify listeners download has a error
     */
    void notifyError(long downloadId, int errorCode, String errorMessage);

    /**
     * register a overall listener to listen all download event
     */
    void registerListener(DownloadListener downloadListener);

    /**
     * register a listener to listen a download with download id
     *
     * @param downloadId download id to listen
     */
    void registerListener(long downloadId, DownloadListener downloadListener);

    /**
     * unregister overall listener
     */
    void unregisterListener(DownloadListener downloadListener);

    void unregisterListener(long downloadId, DownloadListener downloadListener);
}
