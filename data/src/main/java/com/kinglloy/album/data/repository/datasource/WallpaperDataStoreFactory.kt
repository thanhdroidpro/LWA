package com.kinglloy.album.data.repository.datasource

import android.content.Context
import com.kinglloy.album.data.cache.LiveWallpaperCacheImpl
import com.kinglloy.album.data.cache.StyleWallpaperCacheImpl
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
@Singleton
class WallpaperDataStoreFactory @Inject
constructor(val context: Context,
            private val liveWallpaperCache: LiveWallpaperCacheImpl,
            private val styleWallpaperCache: StyleWallpaperCacheImpl) {

    fun createManageDataStore(): WallpaperDataStore =
            WallpaperManageDataStore(context)

    fun createLiveDataStore(): WallpaperDataStore =
            LiveWallpaperDataStoreImpl(context, liveWallpaperCache)


    fun createRemoteLiveDataStore(): WallpaperDataStore {
        return LiveRemoteWallpaperDataStore(context,
                LiveWallpaperDataStoreImpl(context, liveWallpaperCache))
    }

    fun createStyleDataStore(): WallpaperDataStore =
            StyleWallpaperDataStoreImpl(context, styleWallpaperCache)

    fun createRemoteStyleDataStore(): WallpaperDataStore {
        return StyleRemoteWallpaperDataStore(context,
                StyleWallpaperDataStoreImpl(context, styleWallpaperCache))
    }

    fun onDataRefresh() {
        liveWallpaperCache.evictAll()
    }
}