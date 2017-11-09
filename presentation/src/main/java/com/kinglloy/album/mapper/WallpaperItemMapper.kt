package com.kinglloy.album.mapper

import com.fernandocejas.arrow.checks.Preconditions
import com.kinglloy.album.domain.Wallpaper
import com.kinglloy.album.model.WallpaperItem
import java.util.ArrayList
import javax.inject.Inject

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
class WallpaperItemMapper @Inject constructor() {

    fun transform(wallpaper: Wallpaper): WallpaperItem {
        Preconditions.checkNotNull(wallpaper, "Wallpaper can not be null.")
        val wallpaperItem = WallpaperItem()
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
        wallpaperItem.wallpaperType = wallpaper.wallpaperType
        wallpaperItem.size = wallpaper.size
        wallpaperItem.price = wallpaper.price
        wallpaperItem.pro = wallpaper.pro
        return wallpaperItem
    }

    fun transformList(wallpaperEntities: List<Wallpaper>): List<WallpaperItem> {
        Preconditions.checkNotNull(wallpaperEntities, "SourceEntity can not be null.")
        val sources = ArrayList<WallpaperItem>()
        for (entity in wallpaperEntities) {
            sources.add(transform(entity))
        }
        return sources
    }
}