package com.kinglloy.album.data.cache

import android.text.TextUtils
import com.kinglloy.album.data.entity.WallpaperEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author jinyalin
 * @since 2017/8/4.
 */
@Singleton
class LiveWallpaperCacheImpl @Inject constructor() : WallpaperCache {
    private var liveWallpapers: List<WallpaperEntity>? = null

    @Synchronized override fun putWallpapers(wallpapers: List<WallpaperEntity>) {
        this.liveWallpapers = wallpapers
    }

    override fun selectPreviewingWallpaper() {
        if (isDirty()) {
            throw IllegalStateException("Cache is dirty.")
        }

        for (wallpaper in liveWallpapers!!) {
            wallpaper.isSelected = wallpaper.isPreviewing
        }
    }

    override fun previewWallpaper(wallpaperId: String) {
        if (isDirty()) {
            throw IllegalStateException("Cache is dirty.")
        }
        for (wallpaper in liveWallpapers!!) {
            wallpaper.isPreviewing = TextUtils.equals(wallpaperId, wallpaper.wallpaperId)
        }
    }

    override fun getWallpapers(): List<WallpaperEntity> {
        if (isDirty()) {
            throw IllegalStateException("Cache is dirty.")
        }
        return ArrayList(liveWallpapers!!)
    }

    override fun getWallpaper(wallpaperId: String): WallpaperEntity? {
        if (!isWallpaperCached(wallpaperId)) {
            throw IllegalStateException("WallpaperId $wallpaperId is not cached.")
        }
        return liveWallpapers!!.firstOrNull { TextUtils.equals(wallpaperId, it.wallpaperId) }
    }


    @Synchronized override fun evictAll() {
        liveWallpapers = null
    }

    override fun isWallpaperCached(wallpaperId: String): Boolean {
        if (isDirty()) {
            return false
        }
        return liveWallpapers!!.any { TextUtils.equals(wallpaperId, it.wallpaperId) }
    }

    override fun isDirty(): Boolean = liveWallpapers != null && !liveWallpapers!!.isEmpty()

    override fun makeDirty() {
        evictAll()
    }

}