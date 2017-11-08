package com.kinglloy.download.data.repository.datastore.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * @author jinyalin
 * @since 2017/5/28.
 */

class DownloadDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "baidu_download.db";

    private static final int VERSION_2017_05_28 = 1;
    private static final int CUR_DATABASE_VERSION = VERSION_2017_05_28;

    private Context mContext;

    interface Tables {
        String DOWNLOAD_REQUEST = DownloadContract.DownloadRequest.TABLE_NAME;
    }

    DownloadDatabase(Context context) {
        super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.DOWNLOAD_REQUEST + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DownloadContract.DownloadRequest.COLUMN_NAME_DOWNLOAD_ID
                + " INTEGER UNIQUE NOT NULL,"
                + DownloadContract.DownloadRequest.COLUMN_NAME_DOWNLOAD_URI + " TEXT,"
                + DownloadContract.DownloadRequest.COLUMN_NAME_DESTINATION_PATH + " TEXT,"
                + DownloadContract.DownloadRequest.COLUMN_NAME_REQUEST_HEADERS + " TEXT,"
                + DownloadContract.DownloadRequest.COLUMN_NAME_MIME_TYPE + " TEXT,"
                + DownloadContract.DownloadRequest.COLUMN_NAME_ALLOW_METERED + " INTEGER,"
                + DownloadContract.DownloadRequest.COLUMN_NAME_DOWNLOADED_SIZE + " INTEGER,"
                + DownloadContract.DownloadRequest.COLUMN_NAME_TOTAL_SIZE + " INTEGER,"
                + DownloadContract.DownloadRequest.COLUMN_NAME_STATE + " INTEGER,"
                + DownloadContract.DownloadRequest.COLUMN_NAME_ERROR_MSG + " TEXT,"
                + DownloadContract.DownloadRequest.COLUMN_NAME_EXTRA + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
