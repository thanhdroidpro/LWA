package com.kinglloy.download.data.repository.datastore;

import com.kinglloy.download.KinglloyDownloader;
import com.kinglloy.download.cache.DownloadRequestCache;
import com.kinglloy.download.cache.DownloadRequestCacheImpl;

/**
 * @author jinyalin
 * @since 2017/5/26.
 */

public class DownloadDataStoreFactory {
    private static DownloadRequestCache downloadRequestCache =
            DownloadRequestCacheImpl.getInstance();

    public static DownloadDataStore createForQuery(long downloadId) {
        if (downloadRequestCache.isCached(downloadId)) {
            return CacheDownloadDataStore.getInstance();
        } else {
            return createDbStore();
        }
    }

    public static DownloadDataStore createForQuery(KinglloyDownloader.Request request) {
        if (downloadRequestCache.isCached(request)) {
            return CacheDownloadDataStore.getInstance();
        } else {
            return createDbStore();
        }
    }

    public static DownloadDataStore createDbStore() {
        return DbDownloadDataStore.getInstance();
    }
}
