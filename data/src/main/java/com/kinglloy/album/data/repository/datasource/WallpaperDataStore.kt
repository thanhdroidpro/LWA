package com.kinglloy.album.data.repository.datasource

import com.kinglloy.album.data.entity.WallpaperEntity
import com.kinglloy.album.domain.WallpaperType
import io.reactivex.Observable

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
interface WallpaperDataStore {

    fun getPreviewWallpaperEntity(): WallpaperEntity

    fun getWallpaperEntities(): Observable<List<WallpaperEntity>>

    fun selectPreviewingWallpaper(): Observable<Boolean>
    fun previewWallpaper(wallpaperId: String, type: WallpaperType): Observable<Boolean>

    fun downloadWallpaper(wallpaperId: String): Observable<Long>

    fun activeService(serviceType: Int): Observable<Boolean>
    fun getActiveService(): Observable<Int>
}