package com.kinglloy.album.data.repository

import android.content.Context
import com.kinglloy.album.data.entity.mapper.AdvanceWallpaperEntityMapper
import com.kinglloy.album.data.repository.datasource.AdvanceWallpaperDataStoreFactory
import com.kinglloy.album.data.repository.datasource.sync.SyncHelper
import com.kinglloy.album.data.repository.datasource.sync.account.Account
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
    init {
        Account.createSyncAccount(context)
        SyncHelper.updateSyncInterval(context)
    }

    override fun getAdvanceWallpapers(): Observable<List<AdvanceWallpaper>> =
            factory.create().getAdvanceWallpapers().map(wallpaperMapper::transformList)

    override fun loadAdvanceWallpapers(): Observable<List<AdvanceWallpaper>> {
        return factory.createRemoteDataStore().getAdvanceWallpapers()
                .map(wallpaperMapper::transformList)
    }

    override fun downloadAdvanceWallpaper(wallpaperId: String): Observable<Long> =
            factory.createRemoteDataStore().downloadWallpaper(wallpaperId)

    override fun selectPreviewingAdvanceWallpaper():
            Observable<Boolean> = factory.create().selectPreviewingWallpaper()

    override fun previewAdvanceWallpaper(wallpaperId: String): Observable<Boolean> =
            factory.create().previewWallpaper(wallpaperId)

    override fun getPreviewAdvanceWallpaper(): AdvanceWallpaper =
            wallpaperMapper.transform(factory.create().getPreviewWallpaperEntity())

    override fun activeService(serviceType: Int): Observable<Boolean>
            = factory.create().activeService(serviceType)

    override fun getActiveService(): Observable<Int> = factory.create().getActiveService()
}