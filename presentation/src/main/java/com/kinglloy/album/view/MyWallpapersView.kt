package com.kinglloy.album.view

import com.kinglloy.album.model.WallpaperItem

/**
 * YaLin
 * On 2017/11/8.
 */
interface MyWallpapersView : LoadingDataView {
    fun renderWallpapers(wallpapers: List<WallpaperItem>)

    fun selectWallpaper(wallpaper: WallpaperItem)

    fun showEmpty()

    fun complete()

    fun deleteWallpapers(wallpapers: List<WallpaperItem>)
}