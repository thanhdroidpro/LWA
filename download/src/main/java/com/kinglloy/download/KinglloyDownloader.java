package com.kinglloy.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Pair;


import com.kinglloy.download.observable.DownloadObservableImpl;
import com.kinglloy.download.schedule.DownloadRequestHunter;

import java.util.ArrayList;
import java.util.List;

/**
 * A downloader that provide enqueue, start, pause, register listener api.
 *
 * @author jinyalin
 * @since 2017/5/26.
 */
public class KinglloyDownloader {

    private final static class ContextHolder {
        @SuppressLint("StaticFieldLeak")
        private static Context APP_CONTEXT;
    }

    private static KinglloyDownloader INSTANCE;

    private DownloadRequestHunter requestHunter = DownloadRequestHunter.getInstance();

    private KinglloyDownloader(Context context) {
        ContextHolder.APP_CONTEXT = context.getApplicationContext();
    }

    /**
     * get the instance of BaiDuDownloader
     */
    public static KinglloyDownloader getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (KinglloyDownloader.class) {
                if (INSTANCE == null) {
                    INSTANCE = new KinglloyDownloader(context);
                }
            }
        }
        return INSTANCE;
    }

    public static Context getContext() {
        return ContextHolder.APP_CONTEXT;
    }

    /**
     * Enqueue a download request. it may not download now, but enqueue download queue.
     * This will return a download id. if the request is the same exist request, then will return
     * the exist request's download id, else return a new id.
     * <p>
     * If some request has the sample url and sample destination path, will be consider to the
     * same request.
     *
     * @param request a download request, must contain a download url
     * @return a download id to op the request
     */
    public long enqueue(Request request) {
        return requestHunter.postRequest(request);
    }

    /**
     * Query a download id by request. -1 if the request does not exist.
     *
     * @param request the request to query
     * @return id > 0 if exist, else  -1
     */
    public long queryId(Request request) {
        return requestHunter.queryId(request);
    }

    /**
     * Query downloaded size for a request by download id. -1 if not find.
     */
    public long queryDownloadedSize(long downloadId) {
        return requestHunter.queryDownloadedSize(downloadId);
    }

    /**
     * Query request total size by download id, -1 if not find.
     */
    public long queryTotalSize(long downloadId) {
        return requestHunter.queryTotalSize(downloadId);
    }

    /**
     * Restart a paused download by download id. If request already start downloading, it will
     * callback some error.
     *
     * @param id download id to start
     * @see DownloadListener#onDownloadError(long downloadId, int errorCode, String errorMessage)
     */
    public void start(long id) {
        requestHunter.startRequest(id);
    }

    public void start(long[] ids) {
        requestHunter.startRequest(ids);
    }

    /**
     * Pause a downloading request.
     *
     * @param id download id to pause
     */
    public void pause(long id) {
        requestHunter.pauseRequest(id);
    }

    public void pause(long[] ids) {
        requestHunter.pauseRequest(ids);
    }

    /**
     * Cancel a downloading request.
     *
     * @param id download id to cancel
     */
    public void cancel(long id) {
        requestHunter.cancelRequest(id);
    }

    public void cancel(long[] ids) {
        requestHunter.cancelRequest(ids);
    }

    /**
     * Get request state by download id. If there isn't a exist request. will throw a Exception.
     *
     * @return the state of request
     * @see com.kinglloy.download.state.DownloadState
     */
    public int getState(long downloadId) {
        return requestHunter.getState(downloadId);
    }

    /**
     * Register a overall listener, it will listen all download request event.
     */
    public void registerListener(DownloadListener listener) {
        DownloadObservableImpl.getInstance().registerListener(listener);
    }

    /**
     * Register a target listener, it just be notify when the target download request event be emit.
     *
     * @param downloadId the download id to listen
     */
    public void registerListener(long downloadId, DownloadListener listener) {
        DownloadObservableImpl.getInstance().registerListener(downloadId, listener);
    }

    /**
     * Unregister the overall listener. You should always invoke the method when activity destroy,
     * or other lifecycle.
     *
     * @param listener the listener to unregister.
     */
    public void unregisterListener(DownloadListener listener) {
        DownloadObservableImpl.getInstance().unregisterListener(listener);
    }

    /**
     * Unregister a target listener.
     */
    public void unregisterListener(long downloadId, DownloadListener listener) {
        DownloadObservableImpl.getInstance().unregisterListener(downloadId, listener);
    }

    /**
     * A download request that can set download url, destination store path.
     * to provide some base info for download task
     */
    public static class Request {
        private Uri mUri;
        private String mDestinationPath;
        private List<Pair<String, String>> mRequestHeaders = new ArrayList<>();

        private String mMimeType;
        private boolean mMeteredAllowed = true;

        /**
         * @param uri the HTTP or HTTPS URI to download.
         */
        public Request(Uri uri) {
            if (uri == null) {
                throw new NullPointerException();
            }
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
                throw new IllegalArgumentException("Can only download HTTP/HTTPS URIs: " + uri);
            }
            mUri = uri;
        }

        public Request(String uriString) {
            mUri = Uri.parse(uriString);
        }

        public Request(Request origin) {
            this(origin.getUri());
            setDestinationPath(origin.mDestinationPath)
                    .setMimeType(origin.mMimeType)
                    .setAllowedOverMetered(origin.mMeteredAllowed);
            for (Pair<String, String> header : origin.getRequestHeaders()) {
                addRequestHeader(header.first, header.second);
            }
        }

        public Uri getUri() {
            return mUri;
        }

        /**
         * Set the local destination for the downloaded file. Must be a file URI to a path on
         * external storage, and the calling application must have the WRITE_EXTERNAL_STORAGE
         * permission.
         * <p>
         * By default, downloads are saved to a generated filename in the shared download cache and
         * may be deleted by the system at any time to reclaim space.
         *
         * @return this object
         */
        public Request setDestinationPath(String destinationPath) {
            mDestinationPath = destinationPath;
            return this;
        }

        public String getDestinationPath() {
            return mDestinationPath;
        }

        /**
         * Add an HTTP header to be included with the download request.  The header will be added to
         * the end of the list.
         *
         * @param header HTTP header name
         * @param value  header value
         * @return this object
         * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2">HTTP/1.1
         * Message Headers</a>
         */
        public Request addRequestHeader(String header, String value) {
            if (header == null) {
                throw new NullPointerException("header cannot be null");
            }
            if (header.contains(":")) {
                throw new IllegalArgumentException("header may not contain ':'");
            }
            if (value == null) {
                value = "";
            }
            mRequestHeaders.add(Pair.create(header, value));
            return this;
        }

        public List<Pair<String, String>> getRequestHeaders() {
            return mRequestHeaders;
        }

        /**
         * Set the MIME content type of this download.  This will override the content type declared
         * in the server's response.
         *
         * @return this object
         * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7">HTTP/1.1
         * Media Types</a>
         */
        public Request setMimeType(String mimeType) {
            mMimeType = mimeType;
            return this;
        }

        public String getMimeType() {
            return mMimeType;
        }

        /**
         * Set whether this download may proceed over a metered network
         * connection. By default, metered networks are allowed.
         *
         * @see ConnectivityManager#isActiveNetworkMetered()
         */
        public Request setAllowedOverMetered(boolean allow) {
            mMeteredAllowed = allow;
            return this;
        }

        public boolean isMeteredAllowed() {
            return mMeteredAllowed;
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 31 * result + mUri.hashCode();
            result = 31 * result + (mDestinationPath != null ? mDestinationPath.hashCode() : 0);
            return result;
        }
    }
}
