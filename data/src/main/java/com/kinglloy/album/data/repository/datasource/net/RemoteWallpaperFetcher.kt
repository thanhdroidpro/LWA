package com.kinglloy.album.data.repository.datasource.net

import android.content.Context
import com.kinglloy.album.data.BuildConfig

/**
 * @author jinyalin
 * @since 2017/11/6.
 */
class RemoteWallpaperFetcher(context: Context) : DataFetcher(context) {

    protected override fun getUrl(): String {
        return BuildConfig.SERVER_WALLPAPER_ENDPOINT + "/lwa"
    }
}