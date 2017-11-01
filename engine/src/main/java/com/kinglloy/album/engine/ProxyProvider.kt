package com.kinglloy.album.engine

import android.content.Context
import android.service.wallpaper.WallpaperService
import com.kinglloy.album.domain.WallpaperType
import com.kinglloy.album.domain.interactor.GetPreviewWallpaper
import com.kinglloy.album.engine.live.BokehRainbowWallpaper
import com.kinglloy.album.engine.style.StyleWallpaperProxy
import java.io.FileInputStream
import javax.inject.Inject

/**
 * @author jinyalin
 * *
 * @since 2017/7/27.
 */

class ProxyProvider @Inject constructor(private val getPreviewWallpaper: GetPreviewWallpaper) {

    fun provideProxy(host: Context): WallpaperService {
        val previewing = getPreviewWallpaper.previewing
        if (previewing.isDefault) {
            return BokehRainbowWallpaper(host)
        }
        if (previewing.wallpaperType == WallpaperType.STYLE) {
            return try {
                StyleWallpaperProxy(host, previewing.storePath)
            } catch (e: Exception) {
                BokehRainbowWallpaper(host)
            }
        } else {
            val proxy = ProxyApi.getProxy(host, previewing.storePath, previewing.providerName)
            if (proxy != null) {
                return proxy
            }
            return BokehRainbowWallpaper(host)
        }
    }
}
