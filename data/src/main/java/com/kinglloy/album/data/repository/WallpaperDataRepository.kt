package com.kinglloy.album.data.repository

import android.content.Context
import com.kinglloy.album.data.entity.mapper.WallpaperEntityMapper
import com.kinglloy.album.data.repository.datasource.WallpaperDataStoreFactory
import com.kinglloy.album.data.repository.datasource.sync.SyncHelper
import com.kinglloy.album.data.repository.datasource.sync.account.Account
import com.kinglloy.album.domain.Wallpaper
import com.kinglloy.album.domain.WallpaperType
import com.kinglloy.album.domain.repository.WallpaperRepository
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author jinyalin
 * @since 2017/7/27.
 */
@Singleton
class WallpaperDataRepository
@Inject constructor(val context: Context,
                    val factory: WallpaperDataStoreFactory,
                    private val wallpaperMapper: WallpaperEntityMapper)
    : WallpaperRepository {

    init {
        Account.createSyncAccount(context)
        SyncHelper.updateSyncInterval(context)
    }

    override fun getLiveWallpapers(): Observable<List<Wallpaper>> =
            factory.createLiveDataStore().getWallpaperEntities().map(wallpaperMapper::transformList)

    override fun getStyleWallpapers(): Observable<MutableList<Wallpaper>> =
            factory.createStyleDataStore().getWallpaperEntities().map(wallpaperMapper::transformList)

    override fun getVideoWallpapers(): Observable<MutableList<Wallpaper>> =
            factory.createVideoDataStore().getWallpaperEntities().map(wallpaperMapper::transformList)

    override fun loadLiveWallpapers(): Observable<List<Wallpaper>> {
        return factory.createRemoteLiveDataStore().getWallpaperEntities()
                .map(wallpaperMapper::transformList)
    }

    override fun loadStyleWallpapers(): Observable<MutableList<Wallpaper>> {
        return factory.createRemoteStyleDataStore().getWallpaperEntities()
                .map(wallpaperMapper::transformList)
    }

    override fun loadVideoWallpapers(): Observable<MutableList<Wallpaper>> {
        return factory.createRemoteVideoDataStore().getWallpaperEntities()
                .map(wallpaperMapper::transformList)
    }

    override fun downloadLiveWallpaper(wallpaperId: String): Observable<Long> =
            factory.createRemoteLiveDataStore().downloadWallpaper(wallpaperId)

    override fun downloadStyleWallpaper(wallpaperId: String): Observable<Long> =
            factory.createRemoteStyleDataStore().downloadWallpaper(wallpaperId)

    override fun downloadVideoWallpaper(wallpaperId: String): Observable<Long> =
            factory.createRemoteVideoDataStore().downloadWallpaper(wallpaperId)

    override fun previewWallpaper(wallpaperId: String, type: WallpaperType): Observable<Boolean> =
            factory.createManageDataStore().previewWallpaper(wallpaperId, type)

    override fun selectPreviewingWallpaper():
            Observable<Boolean> = factory.createManageDataStore().selectPreviewingWallpaper()

    override fun getPreviewingWallpaper(): Wallpaper =
            wallpaperMapper.transform(factory.createManageDataStore().getPreviewWallpaperEntity())

    override fun activeService(serviceType: Int): Observable<Boolean>
            = factory.createManageDataStore().activeService(serviceType)

    override fun getActiveService(): Observable<Int> = factory.createManageDataStore().getActiveService()
}