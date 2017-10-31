package com.kinglloy.album.data.repository.datasource

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.kinglloy.album.data.cache.WallpaperCache
import com.kinglloy.album.data.entity.WallpaperEntity
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import com.kinglloy.album.data.utils.notifyChange
import io.reactivex.Observable

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
class LiveWallpaperDataStoreImpl(context: Context,
                                 private val wallpaperCache: WallpaperCache)
    : BaseWallpaperDataStore(context) {
    companion object {
        val TAG = "AdvanceDataStore"

        val DEFAULT_WALLPAPER_ID = "-1"
    }

    @Synchronized override fun getPreviewWallpaperEntity(): WallpaperEntity {
        var cursor: Cursor? = null
        var entity: WallpaperEntity? = null
        try {
            val contentResolver = context.contentResolver
            cursor = contentResolver.query(AlbumContract.LiveWallpaper.CONTENT_PREVIEWING_URI,
                    null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                entity = WallpaperEntity.liveWallpaperValue(cursor)
            }
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
        if (entity == null) {
            entity = buildDefaultWallpaper()
        }
        return entity
    }

    override fun getWallpaperEntities(): Observable<List<WallpaperEntity>> =
            createAdvanceWallpapersFromDB().doOnNext(wallpaperCache::putWallpapers)

    override fun selectPreviewingWallpaper():
            Observable<Boolean> {
        return Observable.create { emitter ->
            val selectValue = ContentValues()
            selectValue.put(AlbumContract.LiveWallpaper.COLUMN_NAME_SELECTED, 1)
            val unselectedValue = ContentValues()
            unselectedValue.put(AlbumContract.LiveWallpaper.COLUMN_NAME_SELECTED, 0)
            // unselected old
            context.contentResolver.update(
                    AlbumContract.LiveWallpaper.CONTENT_SELECTED_URI,
                    unselectedValue, null, null)
            // select new
            val uri = AlbumContract.LiveWallpaper.CONTENT_SELECT_PREVIEWING_URI
            val selectedCount = context.contentResolver.update(uri, selectValue, null, null)
            if (selectedCount > 0) {
                emitter.onNext(true)
            } else {
                emitter.onNext(false)
            }
            synchronized(wallpaperCache) {
                if (!wallpaperCache.isDirty()) {
                    wallpaperCache.selectPreviewingWallpaper()
                }
            }

            emitter.onComplete()
            notifyChange(context, AlbumContract.LiveWallpaper.CONTENT_SELECT_PREVIEWING_URI)
        }
    }

    override fun previewWallpaper(wallpaperId: String): Observable<Boolean> {
        return Observable.create { emitter ->
            val previewingValue = ContentValues()
            previewingValue.put(AlbumContract.LiveWallpaper.COLUMN_NAME_PREVIEWING, 1)
            val unpreviewValue = ContentValues()
            unpreviewValue.put(AlbumContract.LiveWallpaper.COLUMN_NAME_PREVIEWING, 0)
            // unpreview old
            context.contentResolver.update(
                    AlbumContract.LiveWallpaper.CONTENT_PREVIEWING_URI,
                    unpreviewValue, null, null)
            // preview new
            val uri = AlbumContract.LiveWallpaper.buildWallpaperUri(wallpaperId)
            val updateCount = context.contentResolver.update(uri, previewingValue, null, null)
            if (updateCount > 0) {
                emitter.onNext(true)
            } else {
                emitter.onNext(false)
            }

            synchronized(wallpaperCache) {
                if (!wallpaperCache.isDirty()) {
                    wallpaperCache.previewWallpaper(wallpaperId)
                }
            }

            emitter.onComplete()
        }
    }

    override fun cancelPreviewing(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelSelect(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun downloadWallpaper(wallpaperId: String): Observable<Long> {
        throw UnsupportedOperationException("Local data store not support download wallpaper.")
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
            cursor = contentResolver.query(AlbumContract.LiveWallpaper
                    .buildWallpaperUri(wallpaperId),
                    null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val entity = WallpaperEntity.liveWallpaperValue(cursor)
                return entity
            }
            return null
        } finally {
            cursor?.close()
        }
    }

    private fun createAdvanceWallpapersFromDB(): Observable<List<WallpaperEntity>> {
        return Observable.create { emitter ->
            var cursor: Cursor? = null
            val validWallpapers = ArrayList<WallpaperEntity>()
            try {
                val contentResolver = context.contentResolver
                cursor = contentResolver.query(AlbumContract.LiveWallpaper.CONTENT_URI,
                        null, null, null, null)
                validWallpapers.addAll(WallpaperEntity.liveWallpaperValues(cursor))
            } finally {
                cursor?.close()
            }

            emitter.onNext(validWallpapers)
            emitter.onComplete()
        }
    }

    private fun buildDefaultWallpaper(): WallpaperEntity {
        val entity = WallpaperEntity()
        entity.isDefault = true
        entity.id = -1
        entity.wallpaperId = DEFAULT_WALLPAPER_ID
        entity.author = "Yalin"
        entity.link = "kinglloy.com"
        entity.name = "Rainbow"

        return entity
    }
}