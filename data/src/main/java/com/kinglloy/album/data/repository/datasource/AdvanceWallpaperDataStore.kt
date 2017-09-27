package com.kinglloy.album.data.repository.datasource

import com.kinglloy.album.data.entity.AdvanceWallpaperEntity
import io.reactivex.Observable

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
interface AdvanceWallpaperDataStore {

    fun getPreviewWallpaperEntity(): AdvanceWallpaperEntity

    fun getAdvanceWallpapers(): Observable<List<AdvanceWallpaperEntity>>

    fun selectPreviewingWallpaper(): Observable<Boolean>
    fun previewWallpaper(wallpaperId: String): Observable<Boolean>

    fun downloadWallpaper(wallpaperId: String): Observable<Long>

    fun activeService(serviceType: Int): Observable<Boolean>
    fun getActiveService(): Observable<Int>
}