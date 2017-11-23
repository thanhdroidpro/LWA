package com.kinglloy.download.data.repository.datastore;

import com.kinglloy.download.KinglloyDownloader;
import com.kinglloy.download.cache.DownloadRequestCache;
import com.kinglloy.download.cache.DownloadRequestCacheImpl;
import com.kinglloy.download.module.InternalRequest;
import com.kinglloy.download.schedule.IdRequest;

/**
 * @author jinyalin
 * @since 2017/6/1.
 */

public class CacheDownloadDataStore implements DownloadDataStore {
    private static class InstanceHolder {
        private static final CacheDownloadDataStore INSTANCE = new CacheDownloadDataStore();
    }

    private DownloadRequestCache mDownloadRequestCache;

    private CacheDownloadDataStore() {
        this.mDownloadRequestCache = DownloadRequestCacheImpl.getInstance();
    }

    public static CacheDownloadDataStore getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void addDownload(IdRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InternalRequest getDownload(long downloadId) {
        if (mDownloadRequestCache.isCached(downloadId)) {
            return mDownloadRequestCache.getDownload(downloadId).getOrigin();
        }
        return null;
    }

    @Override
    public long queryDownloadId(KinglloyDownloader.Request request) {
        if (mDownloadRequestCache.isCached(request)) {
            return mDownloadRequestCache.queryDownloadId(request);
        }
        return -1;
    }

    @Override
    public void removeDownload(IdRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateProgress(IdRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateState(IdRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void pauseAll() {
        throw new UnsupportedOperationException();
    }
}
