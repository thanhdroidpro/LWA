package com.kinglloy.album.engine

import android.content.Context
import android.service.wallpaper.WallpaperService
import com.kinglloy.album.domain.interactor.GetPreviewAdvanceWallpaper
import com.kinglloy.album.engine.advance.BokehRainbowWallpaper
import javax.inject.Inject

/**
 * @author jinyalin
 * *
 * @since 2017/7/27.
 */

class ProxyProvider @Inject constructor(val getPreviewAdvanceWallpaper: GetPreviewAdvanceWallpaper) {

    fun provideProxy(host: Context): WallpaperService {
        val previewing = getPreviewAdvanceWallpaper.previewing
        if (previewing.isDefault) {
            return BokehRainbowWallpaper(host)
        }
        val proxy = ProxyApi.getProxy(host, previewing.storePath, previewing.providerName)
        if (proxy != null) {
            return proxy
        }
        return BokehRainbowWallpaper(host)
    }
}
