package com.kinglloy.album.view

import com.kinglloy.album.model.AdvanceWallpaperItem

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
interface WallpaperListView : LoadingDataView {
    fun renderWallpapers(wallpapers: List<AdvanceWallpaperItem>)

    fun selectWallpaper(wallpaper: AdvanceWallpaperItem)

    fun showEmpty()

    fun complete()

    fun wallpaperSelected(wallpaperId: String)

    fun showDownloadHintDialog(item: AdvanceWallpaperItem)

    fun showDownloadingDialog(item: AdvanceWallpaperItem)

    fun updateDownloadingProgress(downloaded: Long)

    fun downloadComplete(item: AdvanceWallpaperItem)

    fun showDownloadError(item: AdvanceWallpaperItem, e: Exception)
}