package com.kinglloy.album.data.repository

import android.content.Context
import com.kinglloy.album.data.entity.mapper.AdvanceWallpaperEntityMapper
import com.kinglloy.album.data.repository.datasource.AdvanceWallpaperDataStoreFactory
import com.kinglloy.album.domain.AdvanceWallpaper
import com.kinglloy.album.domain.repository.WallpaperRepository
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author jinyalin
 * @since 2017/7/27.
 */
@Singleton
class AdvanceWallpaperDataRepository
@Inject constructor(val context: Context,
                    val factory: AdvanceWallpaperDataStoreFactory,
                    val wallpaperMapper: AdvanceWallpaperEntityMapper)
    : WallpaperRepository {
    override fun getAdvanceWallpapers(): Observable<List<AdvanceWallpaper>> {
        return factory.create().getAdvanceWallpapers().map(wallpaperMapper::transformList)
    }

    override fun loadAdvanceWallpapers(): Observable<List<AdvanceWallpaper>> {
        return factory.createRemoteDataStore().getAdvanceWallpapers()
                .map(wallpaperMapper::transformList)
    }

    override fun downloadAdvanceWallpaper(wallpaperId: String): Observable<Long> {
        return factory.createRemoteDataStore().downloadWallpaper(wallpaperId)
    }

    override fun selectPreviewingAdvanceWallpaper():
            Observable<Boolean> {
        return factory.create().selectPreviewingWallpaper()
    }

    override fun previewAdvanceWallpaper(wallpaperId: String): Observable<Boolean> {
        return factory.create().previewWallpaper(wallpaperId)
    }

    override fun getPreviewAdvanceWallpaper(): AdvanceWallpaper {
        return wallpaperMapper.transform(factory.create().getPreviewWallpaperEntity())
    }
}