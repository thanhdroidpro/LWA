package com.kinglloy.album.data.cache

import com.kinglloy.album.data.entity.AdvanceWallpaperEntity

/**
 * @author jinyalin
 * @since 2017/8/4.
 */
interface AdvanceWallpaperCache {
    fun put(wallpapers: List<AdvanceWallpaperEntity>)

    fun selectPreviewingWallpaper()

    fun previewWallpaper(wallpaperId: String)

    fun getWallpapers(): List<AdvanceWallpaperEntity>

    fun getWallpaper(wallpaperId: String): AdvanceWallpaperEntity?

    fun evictAll()

    fun isCached(wallpaperId: String): Boolean

    fun isDirty(): Boolean

    fun makeDirty()
}