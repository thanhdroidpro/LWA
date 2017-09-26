package com.kinglloy.album.data.repository.datasource

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.kinglloy.album.data.cache.AdvanceWallpaperCache
import com.kinglloy.album.data.entity.AdvanceWallpaperEntity
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import com.kinglloy.album.data.utils.notifyChange
import io.reactivex.Observable

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
class AdvanceWallpaperDataStoreImpl(val context: Context,
                                    val advanceWallpaperCache: AdvanceWallpaperCache)
    : AdvanceWallpaperDataStore {
    companion object {
        val TAG = "AdvanceDataStore"

        val DEFAULT_WALLPAPER_ID = "-1"
    }

    @Synchronized override fun getPreviewWallpaperEntity(): AdvanceWallpaperEntity {
        var cursor: Cursor? = null
        var entity: AdvanceWallpaperEntity? = null
        try {
            val contentResolver = context.contentResolver
            cursor = contentResolver.query(AlbumContract.AdvanceWallpaper.CONTENT_PREVIEWING_URI,
                    null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                entity = AdvanceWallpaperEntity.readEntityFromCursor(cursor)
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

    override fun getAdvanceWallpapers(): Observable<List<AdvanceWallpaperEntity>> {
        return createAdvanceWallpapersFromDB().doOnNext(advanceWallpaperCache::put)
    }

    override fun selectPreviewingWallpaper():
            Observable<Boolean> {
        return Observable.create { emitter ->
            val selectValue = ContentValues()
            selectValue.put(AlbumContract.AdvanceWallpaper.COLUMN_NAME_SELECTED, 1)
            val unselectedValue = ContentValues()
            unselectedValue.put(AlbumContract.AdvanceWallpaper.COLUMN_NAME_SELECTED, 0)
            // unselected old
            context.contentResolver.update(
                    AlbumContract.AdvanceWallpaper.CONTENT_SELECTED_URI,
                    unselectedValue, null, null)
            // select new
            val uri = AlbumContract.AdvanceWallpaper.CONTENT_SELECT_PREVIEWING_URI
            val selectedCount = context.contentResolver.update(uri, selectValue, null, null)
            if (selectedCount > 0) {
                emitter.onNext(true)
            } else {
                emitter.onNext(false)
            }
            synchronized(advanceWallpaperCache) {
                if (!advanceWallpaperCache.isDirty()) {
                    advanceWallpaperCache.selectPreviewingWallpaper()
                }
            }

            emitter.onComplete()
            notifyChange(context, AlbumContract.AdvanceWallpaper.CONTENT_SELECT_PREVIEWING_URI)
        }
    }

    override fun previewWallpaper(wallpaperId: String): Observable<Boolean> {
        return Observable.create { emitter ->
            val previewingValue = ContentValues()
            previewingValue.put(AlbumContract.AdvanceWallpaper.COLUMN_NAME_PREVIEWING, 1)
            val unpreviewValue = ContentValues()
            unpreviewValue.put(AlbumContract.AdvanceWallpaper.COLUMN_NAME_PREVIEWING, 0)
            // unpreview old
            context.contentResolver.update(
                    AlbumContract.AdvanceWallpaper.CONTENT_PREVIEWING_URI,
                    unpreviewValue, null, null)
            // preview new
            val uri = AlbumContract.AdvanceWallpaper.buildWallpaperUri(wallpaperId)
            val selectedCount = context.contentResolver.update(uri, previewingValue, null, null)
            if (selectedCount > 0) {
                emitter.onNext(true)
            } else {
                emitter.onNext(false)
            }

            synchronized(advanceWallpaperCache) {
                if (!advanceWallpaperCache.isDirty()) {
                    advanceWallpaperCache.previewWallpaper(wallpaperId)
                }
            }

            emitter.onComplete()
        }
    }

    override fun downloadWallpaper(wallpaperId: String): Observable<Long> {
        throw UnsupportedOperationException("Local data store not support download wallpaper.")
    }

    fun loadWallpaperEntity(wallpaperId: String): AdvanceWallpaperEntity {
        var entity: AdvanceWallpaperEntity? = null
        synchronized(advanceWallpaperCache) {
            if (!advanceWallpaperCache.isDirty()
                    && advanceWallpaperCache.isCached(wallpaperId)) {
                entity = advanceWallpaperCache.getWallpaper(wallpaperId)
            } else {
                entity = loadWallpaperEntityFromDB(wallpaperId)
            }
            if (entity == null) {
                entity = loadWallpaperEntityFromDB(wallpaperId)
            }
        }
        return entity!!
    }

    private fun loadWallpaperEntityFromDB(wallpaperId: String): AdvanceWallpaperEntity? {
        var cursor: Cursor? = null
        try {
            val contentResolver = context.contentResolver
            cursor = contentResolver.query(AlbumContract.AdvanceWallpaper
                    .buildWallpaperUri(wallpaperId),
                    null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val entity = AdvanceWallpaperEntity.readEntityFromCursor(cursor)
                return entity
            }
            return null
        } finally {
            cursor?.close()
        }
    }

    private fun createAdvanceWallpapersFromDB(): Observable<List<AdvanceWallpaperEntity>> {
        return Observable.create { emitter ->
            var cursor: Cursor? = null
            val validWallpapers = ArrayList<AdvanceWallpaperEntity>()
            try {
                val contentResolver = context.contentResolver
                cursor = contentResolver.query(AlbumContract.AdvanceWallpaper.CONTENT_URI,
                        null, null, null, null)
                validWallpapers.addAll(AdvanceWallpaperEntity.readCursor(cursor))
            } finally {
                cursor?.close()
            }

            emitter.onNext(validWallpapers)
            emitter.onComplete()
        }
    }

    private fun buildDefaultWallpaper(): AdvanceWallpaperEntity {
        val entity = AdvanceWallpaperEntity()
        entity.isDefault = true
        entity.id = -1
        entity.wallpaperId = DEFAULT_WALLPAPER_ID
        entity.author = "Yalin"
        entity.link = "kinglloy.com"
        entity.name = "Rainbow"

        return entity
    }
}