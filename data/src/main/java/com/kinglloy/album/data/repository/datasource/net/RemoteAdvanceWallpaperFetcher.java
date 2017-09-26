package com.kinglloy.album.data.repository.datasource.net;

import android.content.Context;

import com.kinglloy.album.data.BuildConfig;


/**
 * @author jinyalin
 * @since 2017/7/28.
 */

public class RemoteAdvanceWallpaperFetcher extends DataFetcher {

    public RemoteAdvanceWallpaperFetcher(Context context) {
        super(context);
    }


    protected String getUrl() {
        return BuildConfig.SERVER_WALLPAPER_ENDPOINT + "/advance";
    }
}
