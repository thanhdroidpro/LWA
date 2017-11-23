package com.kinglloy.download.data.repository.datastore;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

import com.kinglloy.download.KinglloyDownloader.Request;
import com.kinglloy.download.cache.DownloadRequestCache;
import com.kinglloy.download.cache.DownloadRequestCacheImpl;
import com.kinglloy.download.data.repository.datastore.provider.DownloadContract.DownloadRequest;
import com.kinglloy.download.data.repository.datastore.provider.DownloadProviderMock;
import com.kinglloy.download.module.InternalRequest;
import com.kinglloy.download.schedule.IdRequest;
import com.kinglloy.download.state.DownloadState;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author jinyalin
 * @since 2017/5/28.
 */

class DbDownloadDataStore implements DownloadDataStore {
    private static class InstanceHolder {
        private static final DbDownloadDataStore INSTANCE = new DbDownloadDataStore();
    }

    public static DbDownloadDataStore getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private DownloadProviderMock mDownloadProvider;
    private DownloadRequestCache mDownloadRequestCache;

    private DbDownloadDataStore() {
        mDownloadProvider = DownloadProviderMock.getInstance();
        mDownloadRequestCache = DownloadRequestCacheImpl.getInstance();
    }

    @Override
    public void addDownload(IdRequest request) {
        Uri uri = DownloadRequest.CONTENT_URI;
        mDownloadProvider.insert(uri, requestToContentValues(request));

        // add to cache
        mDownloadRequestCache.add(request);
    }

    @Override
    public InternalRequest getDownload(long downloadId) {
        Uri uri = DownloadRequest.buildDownloadRequestUri(downloadId);
        Cursor cursor = null;
        InternalRequest internalRequest = null;
        try {
            cursor = mDownloadProvider.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                internalRequest = cursorToRequest(cursor).getOrigin();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (internalRequest != null) {
            // add to cache
            mDownloadRequestCache.add(new IdRequest(internalRequest));
        }

        return internalRequest;
    }

    @Override
    public long queryDownloadId(Request request) {
        Uri uri = DownloadRequest.QUERY_ID_URI;
        String querySelection;
        String[] selectionArgs;

        querySelection = DownloadRequest.COLUMN_NAME_DOWNLOAD_URI + " = ? and "
                + DownloadRequest.COLUMN_NAME_DESTINATION_PATH + " = ?";
        selectionArgs = new String[]{request.getUri().toString(),
                request.getDestinationPath()};

        Cursor cursor = null;
        try {
            cursor = mDownloadProvider.query(uri,
                    new String[]{DownloadRequest.COLUMN_NAME_DOWNLOAD_ID},
                    querySelection,
                    selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
            return -1;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void removeDownload(IdRequest request) {

    }

    @Override
    public void updateProgress(IdRequest request) {
        InternalRequest origin = request.getOrigin();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DownloadRequest.COLUMN_NAME_DOWNLOADED_SIZE, origin.getDownloadedSize());
        contentValues.put(DownloadRequest.COLUMN_NAME_TOTAL_SIZE, origin.getTotalSize());

        Uri uri = DownloadRequest.PROGRESS_URI;
        mDownloadProvider.update(uri, contentValues,
                DownloadRequest.COLUMN_NAME_DOWNLOAD_ID + " = ? ",
                new String[]{String.valueOf(request.getId())});

        // update cache
        mDownloadRequestCache.updateProgress(
                request, origin.getDownloadedSize(), origin.getTotalSize());
    }

    @Override
    public void updateState(IdRequest request) {
        InternalRequest origin = request.getOrigin();
        if (origin.getDownloadState() == DownloadState.STATE_CANCEL) {
            Uri uri = DownloadRequest.STATE_URI;
            mDownloadProvider.delete(uri, DownloadRequest.COLUMN_NAME_DOWNLOAD_ID + " = ? ",
                    new String[]{String.valueOf(request.getId())});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DownloadRequest.COLUMN_NAME_STATE, origin.getDownloadState());
            contentValues.put(DownloadRequest.COLUMN_NAME_ERROR_MSG, origin.getErrorMsg());

            Uri uri = DownloadRequest.STATE_URI;
            mDownloadProvider.update(uri, contentValues,
                    DownloadRequest.COLUMN_NAME_DOWNLOAD_ID + " = ? ",
                    new String[]{String.valueOf(request.getId())});
        }

        // update cache
        mDownloadRequestCache.updateState(request, origin.getDownloadState());
    }

    @Override
    public void pauseAll() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DownloadRequest.COLUMN_NAME_STATE,
                DownloadState.STATE_PAUSE);

        Uri uri = DownloadRequest.PAUSE_ALL_URI;
        mDownloadProvider.update(uri, contentValues,
                DownloadRequest.COLUMN_NAME_STATE + " = ? or "
                        + DownloadRequest.COLUMN_NAME_STATE + " = ? ",
                new String[]{String.valueOf(DownloadState.STATE_DOWNLOADING)
                        , String.valueOf(DownloadState.STATE_PENDING)});

        mDownloadRequestCache.evictAll();
    }

    private static ContentValues requestToContentValues(IdRequest request) {
        Request origin = request.getOrigin();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DownloadRequest.COLUMN_NAME_DOWNLOAD_ID, request.getId());
        contentValues.put(DownloadRequest.COLUMN_NAME_DOWNLOAD_URI, origin.getUri().toString());
        contentValues.put(DownloadRequest.COLUMN_NAME_DESTINATION_PATH, origin.getDestinationPath());
        if (!origin.getRequestHeaders().isEmpty()) {
            contentValues.put(DownloadRequest.COLUMN_NAME_REQUEST_HEADERS,
                    encodeHttpHeaders(origin.getRequestHeaders()));
        }
        contentValues.put(DownloadRequest.COLUMN_NAME_MIME_TYPE, origin.getMimeType());
        contentValues.put(DownloadRequest.COLUMN_NAME_ALLOW_METERED,
                origin.isMeteredAllowed() ? 1 : 0);
        return contentValues;
    }

    private static IdRequest cursorToRequest(Cursor cursor) {
        String uri = cursor.getString(
                cursor.getColumnIndex(DownloadRequest.COLUMN_NAME_DOWNLOAD_URI));
        String destinationPath = cursor.getString(
                cursor.getColumnIndex(DownloadRequest.COLUMN_NAME_DESTINATION_PATH));
        String requestHeaderString = cursor.getString(
                cursor.getColumnIndex(DownloadRequest.COLUMN_NAME_REQUEST_HEADERS));
        String mime = cursor.getString(
                cursor.getColumnIndex(DownloadRequest.COLUMN_NAME_MIME_TYPE));
        boolean allowMetered = cursor.getInt(
                cursor.getColumnIndex(DownloadRequest.COLUMN_NAME_ALLOW_METERED)) == 1;
        long downloadedSize = cursor.getLong(
                cursor.getColumnIndex(DownloadRequest.COLUMN_NAME_DOWNLOADED_SIZE));
        long totalSize = cursor.getLong(
                cursor.getColumnIndex(DownloadRequest.COLUMN_NAME_TOTAL_SIZE));
        int state = cursor.getInt(cursor.getColumnIndex(DownloadRequest.COLUMN_NAME_STATE));
        String errorMsg = cursor.getString(
                cursor.getColumnIndex(DownloadRequest.COLUMN_NAME_ERROR_MSG));
        String extra = cursor.getString(
                cursor.getColumnIndex(DownloadRequest.COLUMN_NAME_EXTRA));
        InternalRequest origin = new InternalRequest(uri);
        origin.setDestinationPath(destinationPath)
                .setMimeType(mime)
                .setAllowedOverMetered(allowMetered);
        origin.setDownloadedSize(downloadedSize)
                .setTotalSize(totalSize)
                .setDownloadState(state)
                .setErrorMsg(errorMsg)
                .setExtra(extra);

        List<Pair<String, String>> headers = decodeHttpHeaders(requestHeaderString);
        if (!headers.isEmpty()) {
            for (Pair<String, String> pair : headers) {
                origin.addRequestHeader(pair.first, pair.second);
            }
        }

        return new IdRequest(origin);
    }


    private static String encodeHttpHeaders(List<Pair<String, String>> headers) {
        JSONObject headerObject = new JSONObject();
        try {
            for (Pair<String, String> header : headers) {
                headerObject.put(header.first, header.second);
            }
        } catch (JSONException ignore) {
        }
        return headerObject.toString();
    }

    private static List<Pair<String, String>> decodeHttpHeaders(String headersString) {
        List<Pair<String, String>> headers = new ArrayList<>();
        if (!TextUtils.isEmpty(headersString)) {
            try {
                JSONObject headerObject = new JSONObject(headersString);
                for (Iterator<String> keysIt = headerObject.keys(); keysIt.hasNext(); ) {
                    String key = keysIt.next();
                    String value = headerObject.optString(key);
                    headers.add(new Pair<>(key, value));
                }
            } catch (JSONException ignore) {
            }
        }
        return headers;
    }
}
