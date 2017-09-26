package com.kinglloy.album.mapper

import com.fernandocejas.arrow.checks.Preconditions
import com.kinglloy.album.domain.AdvanceWallpaper
import com.kinglloy.album.model.AdvanceWallpaperItem
import java.util.ArrayList
import javax.inject.Inject

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
class AdvanceWallpaperItemMapper @Inject constructor() {

    fun transform(wallpaper: AdvanceWallpaper): AdvanceWallpaperItem {
        Preconditions.checkNotNull(wallpaper, "Wallpaper can not be null.")
        val wallpaperItem = AdvanceWallpaperItem()
        wallpaperItem.id = wallpaper.id
        wallpaperItem.wallpaperId = wallpaper.wallpaperId
        wallpaperItem.link = wallpaper.link
        wallpaperItem.name = wallpaper.name
        wallpaperItem.author = wallpaper.author
        wallpaperItem.iconUrl = wallpaper.iconUrl
        wallpaperItem.downloadUrl = wallpaper.downloadUrl
        wallpaperItem.providerName = wallpaper.providerName
        wallpaperItem.storePath = wallpaper.storePath
        wallpaperItem.isSelected = wallpaper.isSelected
        wallpaperItem.lazyDownload = wallpaper.lazyDownload
        return wallpaperItem
    }

    fun transformList(wallpaperEntities: List<AdvanceWallpaper>): List<AdvanceWallpaperItem> {
        Preconditions.checkNotNull(wallpaperEntities, "SourceEntity can not be null.")
        val sources = ArrayList<AdvanceWallpaperItem>()
        for (entity in wallpaperEntities) {
            sources.add(transform(entity))
        }
        return sources
    }
}