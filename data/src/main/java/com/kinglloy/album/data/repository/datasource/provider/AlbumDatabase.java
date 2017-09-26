package com.kinglloy.album.data.repository.datasource.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.kinglloy.album.data.R;
import com.kinglloy.album.data.log.LogUtil;
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract.AdvanceWallpaper;
import com.kinglloy.album.data.repository.datasource.sync.account.Account;


/**
 * YaLin 2016/12/30.
 */

public class AlbumDatabase extends SQLiteOpenHelper {

    private static final String TAG = "AlbumDatabase";
    private static final String DATABASE_NAME = "album.db";

    private static final int VERSION_2017_9_26 = 1;
    private static final int CUR_DATABASE_VERSION = VERSION_2017_9_26;

    private final Context mContext;

    interface Tables {
        String ADVANCE_WALLPAPER = AdvanceWallpaper.TABLE_NAME;
    }

    public AlbumDatabase(Context context) {
        super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.ADVANCE_WALLPAPER + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AdvanceWallpaper.COLUMN_NAME_WALLPAPER_ID + " TEXT,"
                + AdvanceWallpaper.COLUMN_NAME_ICON_URL + " TEXT,"
                + AdvanceWallpaper.COLUMN_NAME_DOWNLOAD_URL + " TEXT,"
                + AdvanceWallpaper.COLUMN_NAME_NAME + " TEXT,"
                + AdvanceWallpaper.COLUMN_NAME_AUTHOR + " TEXT,"
                + AdvanceWallpaper.COLUMN_NAME_STORE_PATH + " TEXT,"
                + AdvanceWallpaper.COLUMN_NAME_LINK + " TEXT,"
                + AdvanceWallpaper.COLUMN_NAME_PROVIDER_NAME + " TEXT,"
                + AdvanceWallpaper.COLUMN_NAME_CHECKSUM + " TEXT,"
                + AdvanceWallpaper.COLUMN_NAME_SELECTED + " INTEGER DEFAULT 0,"
                + AdvanceWallpaper.COLUMN_NAME_LAZY_DOWNLOAD + " INTEGER DEFAULT 1,"
                + AdvanceWallpaper.COLUMN_NAME_PREVIEWING + " INTEGER NOT NULL DEFAULT 0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.D(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
        // Cancel any sync currently in progress
        android.accounts.Account account = Account.getAccount();
        if (account != null) {
            LogUtil.D(TAG, "Cancelling any pending syncs for account");
            ContentResolver.cancelSync(account, mContext.getString(R.string.authority));
        }
        int version = oldVersion;

        if (version != CUR_DATABASE_VERSION) {
            LogUtil.E(TAG, "Upgrade unsuccessful -- destroying old data during upgrade");

            db.execSQL("DROP TABLE IF EXISTS " + Tables.ADVANCE_WALLPAPER);
            onCreate(db);
            version = CUR_DATABASE_VERSION;
        }
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
