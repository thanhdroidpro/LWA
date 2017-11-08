package com.kinglloy.download.cache;

import com.kinglloy.download.KinglloyDownloader.Request;
import com.kinglloy.download.schedule.IdRequest;

/**
 * @author jinyalin
 * @since 2017/6/1.
 */

public interface DownloadRequestCache {
    void add(IdRequest request);

    boolean isCached(long downloadId);

    boolean isCached(Request request);

    IdRequest getDownload(long downloadId);

    long queryDownloadId(Request request);

    void updateProgress(IdRequest request, long newSoFar, long total);

    void updateState(IdRequest request, int newState);

    boolean isDirty();

    void evictAll();
}
