package com.kinglloy.album.data.cache

import com.kinglloy.album.data.entity.WallpaperEntity

/**
 * @author jinyalin
 * @since 2017/8/4.
 */
interface WallpaperCache {
    fun putWallpapers(wallpapers: List<WallpaperEntity>)

    fun selectPreviewingWallpaper()

    fun previewWallpaper(wallpaperId: String)

    fun getWallpapers(): List<WallpaperEntity>

    fun getWallpaper(wallpaperId: String): WallpaperEntity?

    fun evictAll()

    fun isWallpaperCached(wallpaperId: String): Boolean

    fun isDirty(): Boolean

    fun makeDirty()
}