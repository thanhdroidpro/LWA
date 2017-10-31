package com.kinglloy.album.data.repository.datasource.net;

import android.content.Context;

import com.kinglloy.album.data.BuildConfig;


/**
 * @author jinyalin
 * @since 2017/7/28.
 */

public class RemoteStyleWallpaperFetcher extends DataFetcher {

    public RemoteStyleWallpaperFetcher(Context context) {
        super(context);
    }


    protected String getUrl() {
        return BuildConfig.SERVER_WALLPAPER_ENDPOINT + "/style_wallpaper";
    }
}
