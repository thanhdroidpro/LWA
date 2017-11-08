package com.kinglloy.download.data.repository.datastore.provider;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.kinglloy.download.utils.LogUtil;
import com.kinglloy.download.utils.SelectionBuilder;
import com.kinglloy.download.KinglloyDownloader;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author jinyalin
 * @since 2017/5/28.
 */
public class DownloadProviderMock {
    private static final class InstanceHolder {
        private static final DownloadProviderMock INSTANCE = new DownloadProviderMock();
    }

    private static final String TAG = "DownloadProviderMock";

    private DownloadDatabase mOpenHelper;
    private DownloadProviderUriMatcher mUriMatcher;

    private DownloadProviderMock() {
        mOpenHelper = new DownloadDatabase(getContext());
        mUriMatcher = new DownloadProviderUriMatcher();
    }

    public static DownloadProviderMock getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private Context getContext() {
        return KinglloyDownloader.getContext();
    }

    private void deleteDatabase() {
        mOpenHelper.close();
        Context context = getContext();
        DownloadDatabase.deleteDatabase(context);
        mOpenHelper = new DownloadDatabase(context);
    }

    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            // TODO: 2017/5/28 need to implement
//            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
//            for (int i = 0; i < numOperations; i++) {
//                results[i] = operations.get(i).apply(this, results, i);
//            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        DownloadUriEnum uriEnum = mUriMatcher.matchUri(uri);

        LogUtil.D(TAG, "uri=" + uri + " code=" + uriEnum.code + " proj=" +
                Arrays.toString(projection) + " selection=" + selection + " args="
                + Arrays.toString(selectionArgs) + ")");

        switch (uriEnum) {
            case DOWNLOAD_REQUEST:
            case DOWNLOAD_REQUEST_ID: {
                final SelectionBuilder builder = buildSimpleSelection(uri);
                return builder.query(db, projection, sortOrder);
            }
            case DOWNLOAD_REQUEST_QUERY_ID: {
                final SelectionBuilder builder = buildSimpleSelection(uri);
                return builder.where(selection, selectionArgs).query(db, projection, sortOrder);
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri for " + uri);
            }
        }
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        LogUtil.D(TAG, "insert(uri=" + uri + ", values=" + values.toString()
                + ")");

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        DownloadUriEnum uriEnum = mUriMatcher.matchUri(uri);
        long id = db.insertOrThrow(uriEnum.table, null, values);

        switch (uriEnum) {
            case DOWNLOAD_REQUEST:
                return DownloadContract.DownloadRequest.buildDownloadRequestUri(
                        values.getAsLong(DownloadContract.DownloadRequest.COLUMN_NAME_DOWNLOAD_ID));
            default: {
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
            }
        }
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        LogUtil.D(TAG, "delete(uri=" + uri + ")");
        if (uri == DownloadContract.BASE_CONTENT_URI) {
            deleteDatabase();
            return 1;
        }
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        return builder.where(selection, selectionArgs).delete(db);
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        LogUtil.D(TAG, "update(uri=" + uri + ")");
        if (uri == DownloadContract.BASE_CONTENT_URI) {
            deleteDatabase();
            return 1;
        }
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        return builder.where(selection, selectionArgs).update(db, values);
    }

    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        DownloadUriEnum uriEnum = mUriMatcher.matchUri(uri);

        switch (uriEnum) {
            case DOWNLOAD_REQUEST:
            case DOWNLOAD_REQUEST_QUERY_ID:
            case DOWNLOAD_REQUEST_PROGRESS:
            case DOWNLOAD_REQUEST_STATE:
            case DOWNLOAD_REQUEST_PAUSE_ALL: {
                return builder.table(uriEnum.table);
            }
            case DOWNLOAD_REQUEST_ID: {
                long downloadId = DownloadContract.DownloadRequest.getDownloadRequestId(uri);
                return builder.table(DownloadDatabase.Tables.DOWNLOAD_REQUEST)
                        .where(DownloadContract.DownloadRequest.COLUMN_NAME_DOWNLOAD_ID + " = ?",
                                String.valueOf(downloadId));
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri for " + uri);
            }
        }
    }
}
