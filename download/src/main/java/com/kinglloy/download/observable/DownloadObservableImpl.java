package com.kinglloy.download.observable;

import android.os.Handler;
import android.os.Looper;

import com.kinglloy.download.DownloadListener;
import com.kinglloy.download.schedule.IdRequest;
import com.kinglloy.download.state.DownloadState;
import com.kinglloy.download.utils.Assertions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A observable impl to store all listeners. And provide notify api to framework.
 *
 * @author jinyalin
 * @since 2017/5/31.
 */
public class DownloadObservableImpl implements DownloadObservable {
    private static class InstanceHolder {
        private static final DownloadObservableImpl INSTANCE = new DownloadObservableImpl();
    }

    private Set<MainHandlerListener> overallListeners
            = Collections.synchronizedSet(new HashSet<MainHandlerListener>());

    private Map<Long, Set<MainHandlerListener>> downloadListeners
            = new ConcurrentHashMap<>();

    private DownloadObservableImpl() {
    }

    public static DownloadObservableImpl getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public synchronized void notifyDownloadStateChange(IdRequest request, int newState) {
        notifyState(overallListeners, request, newState);

        if (downloadListeners.containsKey(request.getId())) {
            Set<MainHandlerListener> listeners = downloadListeners.get(request.getId());
            notifyState(listeners, request, newState);
        }
    }

    private void notifyState(Set<MainHandlerListener> listeners, IdRequest request, int newState) {
        for (MainHandlerListener listener : listeners) {
            if (newState == DownloadState.STATE_PENDING) {
                listener.onDownloadPending(request.getId());
            } else if (newState == DownloadState.STATE_PAUSE) {
                listener.onDownloadPause(request.getId());
            } else if (newState == DownloadState.STATE_COMPLETE) {
                listener.onDownloadComplete(request.getId(),
                        request.getOrigin().getDestinationPath());
            }
        }
    }

    @Override
    public synchronized void notifyDownloadProgress(IdRequest request, long downloadedSize, long totalSize) {
        for (MainHandlerListener listener : overallListeners) {
            listener.onDownloadProgress(request.getId(), downloadedSize, totalSize);
        }
        if (downloadListeners.containsKey(request.getId())) {
            Set<MainHandlerListener> listeners = downloadListeners.get(request.getId());
            for (MainHandlerListener listener : listeners) {
                listener.onDownloadProgress(request.getId(), downloadedSize, totalSize);
            }
        }
    }

    @Override
    public synchronized void notifyError(IdRequest request, int errorCode, String errorMessage) {
        notifyError(request.getId(), errorCode, errorMessage);
    }

    @Override
    public synchronized void notifyError(long downloadId, int errorCode, String errorMessage) {
        for (MainHandlerListener listener : overallListeners) {
            listener.onDownloadError(downloadId, errorCode, errorMessage);
        }
        if (downloadListeners.containsKey(downloadId)) {
            Set<MainHandlerListener> listeners = downloadListeners.get(downloadId);
            for (MainHandlerListener listener : listeners) {
                listener.onDownloadError(downloadId, errorCode, errorMessage);
            }
        }
    }

    @Override
    public synchronized void registerListener(DownloadListener downloadListener) {
        overallListeners.add(new MainHandlerListener(downloadListener));
    }

    @Override
    public synchronized void registerListener(long downloadId, DownloadListener downloadListener) {
        if (!downloadListeners.containsKey(downloadId)) {
            Set<MainHandlerListener> idSet = new HashSet<>();
            idSet.add(new MainHandlerListener(downloadListener));
            downloadListeners.put(downloadId, idSet);
        } else {
            Set<MainHandlerListener> idSet = downloadListeners.get(downloadId);
            idSet.add(new MainHandlerListener(downloadListener));
        }
    }

    @Override
    public synchronized void unregisterListener(DownloadListener downloadListener) {
        overallListeners.remove(new MainHandlerListener(downloadListener));
    }

    @Override
    public synchronized void unregisterListener(long downloadId, DownloadListener downloadListener) {
        if (downloadListeners.containsKey(downloadId)) {
            Set<MainHandlerListener> idSet = downloadListeners.get(downloadId);
            idSet.remove(new MainHandlerListener(downloadListener));
        }
    }

    private static class MainHandlerListener implements DownloadListener {
        private static final Handler mMainHandler = new Handler(Looper.getMainLooper());

        private DownloadListener origin;

        MainHandlerListener(DownloadListener origin) {
            Assertions.checkNotNull(origin);
            this.origin = origin;
        }

        @Override
        public void onDownloadPending(final long downloadId) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    origin.onDownloadPending(downloadId);
                }
            });
        }

        @Override
        public void onDownloadProgress(final long downloadId,
                                       final long downloadedSize, final long totalSize) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    origin.onDownloadProgress(downloadId, downloadedSize, totalSize);
                }
            });
        }

        @Override
        public void onDownloadPause(final long downloadId) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    origin.onDownloadPause(downloadId);
                }
            });
        }

        @Override
        public void onDownloadComplete(final long downloadId, final String path) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    origin.onDownloadComplete(downloadId, path);
                }
            });
        }

        @Override
        public void onDownloadError(final long downloadId,
                                    final int errorCode, final String errorMessage) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    origin.onDownloadError(downloadId, errorCode, errorMessage);
                }
            });
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof MainHandlerListener
                    && origin.equals(((MainHandlerListener) obj).origin);
        }

        @Override
        public int hashCode() {
            return origin.hashCode();
        }
    }
}
