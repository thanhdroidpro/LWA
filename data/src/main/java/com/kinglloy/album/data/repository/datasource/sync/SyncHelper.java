package com.kinglloy.album.data.repository.datasource.sync;


import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;


import com.google.gson.JsonParser;
import com.kinglloy.album.data.BuildConfig;
import com.kinglloy.album.data.SyncConfig;
import com.kinglloy.album.data.log.LogUtil;
import com.kinglloy.album.data.repository.datasource.io.AdvanceWallpaperHandler;
import com.kinglloy.album.data.repository.datasource.net.RemoteAdvanceWallpaperFetcher;
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract;

import java.io.IOException;
import java.util.ArrayList;

/**
 * YaLin 2017/1/3.
 */

public class SyncHelper {

    private static final String TAG = "SyncHelper";
    private final Context mContext;

    private AdvanceWallpaperHandler mDataHandler;

    public SyncHelper(Context context) {
        mContext = context;
        mDataHandler = new AdvanceWallpaperHandler(mContext);
    }

    public boolean performSync(SyncResult syncResult, Bundle extras) {
        try {
            doStyleSync();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean doStyleSync() throws IOException {
        if (!isOnline(mContext)) {
            LogUtil.D(TAG, "Not attempting remote sync because device is OFFLINE");
            return false;
        }

        LogUtil.D(TAG, "Starting remote sync.");
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();
        String data = new RemoteAdvanceWallpaperFetcher(mContext).fetchDataIfNewer();
        if (!TextUtils.isEmpty(data)) {
            JsonParser parser = new JsonParser();
            mDataHandler.process(parser.parse(data));
            mDataHandler.makeContentProviderOperations(batch);
        }

        try {
            int operations = batch.size();
            if (operations > 0) {
                mContext.getContentResolver().applyBatch(AlbumContract.AUTHORITY, batch);
            }
        } catch (RemoteException ex) {
            LogUtil.D(TAG, "RemoteException while applying content provider operations.");
            throw new RuntimeException("Error executing content provider batch operation", ex);
        } catch (OperationApplicationException ex) {
            LogUtil.D(TAG, "OperationApplicationException while applying content provider operations.");
            throw new RuntimeException("Error executing content provider batch operation", ex);
        }

        return true;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static void updateSyncInterval(final Context context) {
        Account account =
                com.kinglloy.album.data.repository.datasource.sync.account.Account.getAccount();
        LogUtil.D(TAG, "Checking sync interval");
        long recommended = calculateRecommendedSyncInterval(context);
        LogUtil.D(TAG, "Setting up sync for account, interval " + recommended + "ms");
        ContentResolver.setIsSyncable(account, AlbumContract.AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(account, AlbumContract.AUTHORITY, true);
        ContentResolver
                .addPeriodicSync(account, AlbumContract.AUTHORITY, new Bundle(), recommended / 1000L);

    }

    private static long calculateRecommendedSyncInterval(final Context context) {
        if (BuildConfig.DEMO_MODE) {
            return SyncConfig.DEBUG_AUTO_SYNC_INTERVAL_LONG;
        } else {
            return SyncConfig.AUTO_SYNC_INTERVAL_LONG;
        }
    }
}
