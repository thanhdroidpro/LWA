package com.kinglloy.download.domain;

import com.kinglloy.download.KinglloyDownloader.Request;
import com.kinglloy.download.module.InternalRequest;
import com.kinglloy.download.schedule.IdRequest;

/**
 * @author jinyalin
 * @since 2017/5/28.
 */

public interface DownloadRepository {
    void addDownload(IdRequest request);

    InternalRequest getDownload(long downloadId);

    long queryDownloadId(Request request);

    void updateProgress(IdRequest idRequest);

    void updateState(IdRequest idRequest);

    void pauseAll();
}
