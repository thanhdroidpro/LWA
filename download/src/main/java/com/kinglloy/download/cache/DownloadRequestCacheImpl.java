package com.kinglloy.download.cache;

import com.kinglloy.download.KinglloyDownloader;
import com.kinglloy.download.schedule.IdRequest;
import com.kinglloy.download.state.DownloadState;
import com.kinglloy.download.utils.LogUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jinyalin
 * @since 2017/6/1.
 */

public class DownloadRequestCacheImpl implements DownloadRequestCache {
    private static final String TAG = "DownloadRequestCache";

    private static class InstanceHolder {
        private static final DownloadRequestCacheImpl INSTANCE = new DownloadRequestCacheImpl();
    }

    private Map<Long, IdRequest> requestMap = new ConcurrentHashMap<>();

    private DownloadRequestCacheImpl() {

    }

    public static DownloadRequestCacheImpl getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void add(IdRequest request) {
        if (!requestMap.containsKey(request.getId())) {
            requestMap.put(request.getId(), request);
        }
    }

    @Override
    public boolean isCached(long downloadId) {
        return requestMap.containsKey(downloadId);
    }

    @Override
    public boolean isCached(KinglloyDownloader.Request request) {
        for (IdRequest idRequest : requestMap.values()) {
            if (idRequest.getOrigin().getUri().equals(request.getUri())
                    && idRequest.getOrigin().getDestinationPath()
                    .equals(request.getDestinationPath())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IdRequest getDownload(long downloadId) {
        if (isCached(downloadId)) {
            LogUtil.D(TAG, "getDownload Request is cached get it from cache id = " + downloadId);
            return requestMap.get(downloadId);
        }
        return null;
    }

    @Override
    public long queryDownloadId(KinglloyDownloader.Request request) {
        for (IdRequest idRequest : requestMap.values()) {
            if (idRequest.getOrigin().getUri().equals(request.getUri())
                    && idRequest.getOrigin().getDestinationPath()
                    .equals(request.getDestinationPath())) {
                LogUtil.D(TAG, "queryDownloadId Request is cached get it from cache id = "
                        + idRequest.getId());
                return idRequest.getId();
            }
        }
        return -1;
    }

    @Override
    public void updateProgress(IdRequest request, long newSoFar, long total) {
        if (isCached(request.getId())) {
            IdRequest cachedRequest = requestMap.get(request.getId());
            cachedRequest.getOrigin().setDownloadedSize(newSoFar);
            cachedRequest.getOrigin().setTotalSize(total);
        }
    }

    @Override
    public void updateState(IdRequest request, int newState) {
        if (isCached(request.getId())) {
            if (request.getOrigin().getDownloadState() == DownloadState.STATE_CANCEL) {
                requestMap.remove(request.getId());
            } else {
                IdRequest cachedRequest = requestMap.get(request.getId());
                cachedRequest.getOrigin().setDownloadState(newState);
            }
        }
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void evictAll() {
        requestMap.clear();
    }
}
