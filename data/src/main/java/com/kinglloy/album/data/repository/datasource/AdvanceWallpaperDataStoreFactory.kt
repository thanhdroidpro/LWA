package com.kinglloy.album.data.repository.datasource

import android.content.Context
import com.kinglloy.album.data.cache.AdvanceWallpaperCache
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
@Singleton
class AdvanceWallpaperDataStoreFactory @Inject
constructor(val context: Context,
            val advanceWallpaperCache: AdvanceWallpaperCache) {

    fun create(): AdvanceWallpaperDataStore =
            AdvanceWallpaperDataStoreImpl(context, advanceWallpaperCache)


    fun createRemoteDataStore(): AdvanceWallpaperDataStore {
        return RemoteAdvanceWallpaperDataStore(context,
                AdvanceWallpaperDataStoreImpl(context, advanceWallpaperCache))
    }

    fun onDataRefresh() {
        advanceWallpaperCache.evictAll()
    }
}