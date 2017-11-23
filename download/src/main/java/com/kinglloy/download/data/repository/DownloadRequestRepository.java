package com.kinglloy.download.data.repository;

import com.kinglloy.download.KinglloyDownloader.Request;
import com.kinglloy.download.data.repository.datastore.DownloadDataStore;
import com.kinglloy.download.data.repository.datastore.DownloadDataStoreFactory;
import com.kinglloy.download.domain.DownloadRepository;
import com.kinglloy.download.module.InternalRequest;
import com.kinglloy.download.schedule.IdRequest;
import com.kinglloy.download.utils.Assertions;

/**
 * @author jinyalin
 * @since 2017/5/26.
 */

public class DownloadRequestRepository implements DownloadRepository {
    private static class InstanceHolder {
        private static final DownloadRepository INSTANCE = new DownloadRequestRepository();
    }

    public static DownloadRepository getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void addDownload(IdRequest request) {
        Assertions.checkNotNull(request);
        DownloadDataStore downloadDataStore = DownloadDataStoreFactory.createDbStore();
        downloadDataStore.addDownload(request);
    }

    @Override
    public InternalRequest getDownload(long downloadId) {
        DownloadDataStore downloadDataStore = DownloadDataStoreFactory.createForQuery(downloadId);
        return downloadDataStore.getDownload(downloadId);
    }

    @Override
    public long queryDownloadId(Request request) {
        Assertions.checkNotNull(request);
        DownloadDataStore downloadDataStore = DownloadDataStoreFactory.createForQuery(request);
        return downloadDataStore.queryDownloadId(request);
    }

    @Override
    public void updateProgress(IdRequest idRequest) {
        DownloadDataStore downloadDataStore = DownloadDataStoreFactory.createDbStore();
        downloadDataStore.updateProgress(idRequest);
    }

    @Override
    public void updateState(IdRequest idRequest) {
        DownloadDataStore downloadDataStore = DownloadDataStoreFactory.createDbStore();
        downloadDataStore.updateState(idRequest);
    }

    @Override
    public void pauseAll() {
        DownloadDataStore downloadDataStore = DownloadDataStoreFactory.createDbStore();
        downloadDataStore.pauseAll();
    }
}
