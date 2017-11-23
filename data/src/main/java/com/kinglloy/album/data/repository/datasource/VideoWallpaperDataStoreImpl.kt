package com.kinglloy.album.data.repository.datasource

import android.content.Context
import android.database.Cursor
import com.kinglloy.album.data.cache.WallpaperCache
import com.kinglloy.album.data.entity.WallpaperEntity
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import com.kinglloy.album.domain.WallpaperType
import io.reactivex.Observable

/**
 * @author jinyalin
 * @since 2017/10/31.
 */
class VideoWallpaperDataStoreImpl(private val context: Context,
                                  private val wallpaperCache: WallpaperCache)
    : WallpaperDataStore {
    override fun getPreviewWallpaperEntity(): WallpaperEntity {
        throw UnsupportedOperationException("Video wallpaper data store not support get wallpaper.")
    }

    override fun getWallpaperEntities(): Observable<List<WallpaperEntity>> =
            createVideoWallpapersFromDB().doOnNext(wallpaperCache::putWallpapers)

    override fun getDownloadedWallpaperEntities(): Observable<List<WallpaperEntity>> {
        throw UnsupportedOperationException("Video wallpaper data store not support get downloaded wallpaper.")
    }

    override fun deleteDownloadedWallpapers(wallpapers: List<WallpaperEntity>): Observable<Boolean> {
        throw UnsupportedOperationException("Video wallpaper data store not support delete downloaded wallpaper.")
    }

    override fun selectPreviewingWallpaper(): Observable<Boolean> {
        throw UnsupportedOperationException("Video wallpaper data store not support select previewing.")
    }

    override fun previewWallpaper(wallpaperId: String, type: WallpaperType): Observable<Boolean> {
        throw UnsupportedOperationException("Video wallpaper data store not support preview.")
    }

    override fun activeService(serviceType: Int): Observable<Boolean> {
        throw UnsupportedOperationException("Video wallpaper data store not support active service.")
    }

    override fun getActiveService(): Observable<Int> {
        throw UnsupportedOperationException("Video wallpaper data store not support get active service.")
    }

    private fun createVideoWallpapersFromDB(): Observable<List<WallpaperEntity>> {
        return Observable.create { emitter ->
            var cursor: Cursor? = null
            val validWallpapers = ArrayList<WallpaperEntity>()
            try {
                val contentResolver = context.contentResolver
                cursor = contentResolver.query(AlbumContract.VideoWallpaper.CONTENT_URI,
                        null, null, null, null)
                validWallpapers.addAll(WallpaperEntity.videoWallpaperValues(cursor))
            } finally {
                cursor?.close()
            }

            emitter.onNext(validWallpapers)
            emitter.onComplete()
        }
    }

    fun loadWallpaperEntity(wallpaperId: String): WallpaperEntity {
        var entity: WallpaperEntity? = null
        synchronized(wallpaperCache) {
            if (!wallpaperCache.isDirty()
                    && wallpaperCache.isWallpaperCached(wallpaperId)) {
                entity = wallpaperCache.getWallpaper(wallpaperId)
            } else {
                entity = loadWallpaperEntityFromDB(wallpaperId)
            }
            if (entity == null) {
                entity = loadWallpaperEntityFromDB(wallpaperId)
            }
        }
        return entity!!
    }

    private fun loadWallpaperEntityFromDB(wallpaperId: String): WallpaperEntity? {
        var cursor: Cursor? = null
        try {
            val contentResolver = context.contentResolver
            cursor = contentResolver.query(AlbumContract.VideoWallpaper
                    .buildWallpaperUri(wallpaperId),
                    null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                return WallpaperEntity.videoWallpaperValue(cursor)
            }
            return null
        } finally {
            cursor?.close()
        }
    }

}