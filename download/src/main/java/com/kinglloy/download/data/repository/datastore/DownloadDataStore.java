package com.kinglloy.download.data.repository.datastore;


import com.kinglloy.download.KinglloyDownloader.Request;
import com.kinglloy.download.module.InternalRequest;
import com.kinglloy.download.schedule.IdRequest;

/**
 * @author jinyalin
 * @since 2017/5/26.
 */

public interface DownloadDataStore {
    void addDownload(IdRequest request);

    InternalRequest getDownload(long downloadId);

    long queryDownloadId(Request request);

    void removeDownload(IdRequest request);

    void updateProgress(IdRequest request);

    void updateState(IdRequest request);

    void pauseAll();
}
