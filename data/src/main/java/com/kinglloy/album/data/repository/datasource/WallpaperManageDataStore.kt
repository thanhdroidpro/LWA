package com.kinglloy.album.data.repository.datasource

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.kinglloy.album.data.cache.WallpaperCache
import com.kinglloy.album.data.entity.WallpaperEntity
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract.PreviewingWallpaper.*
import com.kinglloy.album.data.utils.WallpaperFileHelper
import com.kinglloy.album.data.utils.notifyChange
import com.kinglloy.album.domain.WallpaperType
import io.reactivex.Observable
import java.io.File

/**
 * @author jinyalin
 * @since 2017/11/1.
 */
class WallpaperManageDataStore(val context: Context, private val caches: ArrayList<WallpaperCache>)
    : WallpaperDataStore {
    @Synchronized override fun getPreviewWallpaperEntity(): WallpaperEntity {
        var entity: WallpaperEntity? = null
        val uri = AlbumContract.PreviewingWallpaper.CONTENT_URI
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        var queryWallpaperUri: Uri? = null
        var type = 0
        try {
            if (cursor != null && cursor.moveToFirst()) {
                type = cursor.getInt(cursor.getColumnIndex(
                        AlbumContract.PreviewingWallpaper.COLUMN_NAME_WALLPAPER_TYPE))
                val wallpaperId = cursor.getString(cursor.getColumnIndex(
                        AlbumContract.PreviewingWallpaper.COLUMN_NAME_WALLPAPER_ID))

                queryWallpaperUri = when (type) {
                    WallpaperType.LIVE.typeInt ->
                        AlbumContract.LiveWallpaper.buildWallpaperUri(wallpaperId)
                    WallpaperType.STYLE.typeInt ->
                        AlbumContract.StyleWallpaper.buildWallpaperUri(wallpaperId)
                    else ->
                        AlbumContract.VideoWallpaper.buildWallpaperUri(wallpaperId)
                }
            }
        } finally {
            cursor?.close()
        }
        if (queryWallpaperUri != null) {
            val wallpaperCursor = context.contentResolver.query(queryWallpaperUri,
                    null, null, null, null)
            try {
                if (wallpaperCursor != null && wallpaperCursor.moveToFirst()) {
                    entity = when (type) {
                        WallpaperType.LIVE.typeInt ->
                            WallpaperEntity.liveWallpaperValue(wallpaperCursor)
                        WallpaperType.STYLE.typeInt ->
                            WallpaperEntity.styleWallpaperValue(wallpaperCursor)
                        else ->
                            WallpaperEntity.videoWallpaperValue(wallpaperCursor)
                    }
                }
            } finally {
                wallpaperCursor?.close()
            }
        }

        if (entity == null) {
            entity = buildDefaultWallpaper()
        }
        return entity
    }

    override fun getWallpaperEntities(): Observable<List<WallpaperEntity>> {
        throw UnsupportedOperationException("Wallpaper manage data store not support get wallpaper entities.")
    }

    override fun getDownloadedWallpaperEntities(): Observable<List<WallpaperEntity>> {
        return Observable.create { emitter ->
            val videoWallpaperUri = AlbumContract.VideoWallpaper.CONTENT_URI
            val liveWallpaperUri = AlbumContract.LiveWallpaper.CONTENT_URI
            val styleWallpaperUri = AlbumContract.StyleWallpaper.CONTENT_URI

            val videoWallpaperCursor = context.contentResolver.query(videoWallpaperUri,
                    null, null, null, null)
            val liveWallpaperCursor = context.contentResolver.query(liveWallpaperUri,
                    null, null, null, null)
            val styleWallpaperCursor = context.contentResolver.query(styleWallpaperUri,
                    null, null, null, null)

            val downloadedWallpapers = ArrayList<WallpaperEntity>()
            try {
                if (videoWallpaperCursor != null && videoWallpaperCursor.moveToFirst()) {
                    val videoWallpapers = WallpaperEntity.videoWallpaperValues(videoWallpaperCursor)
                    downloadedWallpapers.addAll(filterDownloadedWallpapers(videoWallpapers))
                }
                if (liveWallpaperCursor != null && liveWallpaperCursor.moveToFirst()) {
                    val liveWallpapers = WallpaperEntity.liveWallpaperValues(liveWallpaperCursor)
                    downloadedWallpapers.addAll(filterDownloadedWallpapers(liveWallpapers))
                }
                if (styleWallpaperCursor != null && styleWallpaperCursor.moveToFirst()) {
                    val styleWallpapers = WallpaperEntity.styleWallpaperValues(styleWallpaperCursor)
                    downloadedWallpapers.addAll(filterDownloadedWallpapers(styleWallpapers))
                }
            } finally {
                videoWallpaperCursor?.close()
                liveWallpaperCursor?.close()
                styleWallpaperCursor?.close()
            }

            emitter.onNext(downloadedWallpapers)
            emitter.onComplete()
        }
    }

    override fun deleteDownloadedWallpapers(filePaths: List<String>): Observable<Boolean> {
        return Observable.create { emitter ->
            for (path in filePaths) {
                File(path).delete()
            }
            notifyChange(context, AlbumContract.LiveWallpaper.CONTENT_DOWNLOAD_ITEM_DELETED_URI)
            notifyChange(context, AlbumContract.StyleWallpaper.CONTENT_DOWNLOAD_ITEM_DELETED_URI)
            notifyChange(context, AlbumContract.VideoWallpaper.CONTENT_DOWNLOAD_ITEM_DELETED_URI)
            emitter.onNext(true)
            emitter.onComplete()
        }
    }

    override fun selectPreviewingWallpaper(): Observable<Boolean> {
        return Observable.create { emitter ->
            val uri = AlbumContract.PreviewingWallpaper.CONTENT_URI
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            val type: Int
            val selectValue = ContentValues()
            var selectPreviewUri: Uri? = null
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    type = cursor.getInt(cursor.getColumnIndex(
                            AlbumContract.PreviewingWallpaper.COLUMN_NAME_WALLPAPER_TYPE))
                    val wallpaperId = cursor.getString(cursor.getColumnIndex(
                            AlbumContract.PreviewingWallpaper.COLUMN_NAME_WALLPAPER_ID))

                    when (type) {
                        WallpaperType.LIVE.typeInt -> {
                            selectPreviewUri = AlbumContract.LiveWallpaper.buildWallpaperUri(wallpaperId)
                            selectValue.put(AlbumContract.LiveWallpaper.COLUMN_NAME_SELECTED, 1)
                        }
                        WallpaperType.STYLE.typeInt -> {
                            selectPreviewUri = AlbumContract.StyleWallpaper.buildWallpaperUri(wallpaperId)
                            selectValue.put(AlbumContract.StyleWallpaper.COLUMN_NAME_SELECTED, 1)
                        }
                        else -> {
                            selectPreviewUri = AlbumContract.VideoWallpaper.buildWallpaperUri(wallpaperId)
                            selectValue.put(AlbumContract.VideoWallpaper.COLUMN_NAME_SELECTED, 1)
                        }
                    }
                }
            } finally {
                cursor?.close()
            }

            val unselectedLiveValue = ContentValues()
            unselectedLiveValue.put(AlbumContract.LiveWallpaper.COLUMN_NAME_SELECTED, 0)
            // unselected live old
            context.contentResolver.update(
                    AlbumContract.LiveWallpaper.CONTENT_SELECTED_URI,
                    unselectedLiveValue, null, null)

            val unselectedStyleValue = ContentValues()
            unselectedStyleValue.put(AlbumContract.StyleWallpaper.COLUMN_NAME_SELECTED, 0)
            // unselected style old
            context.contentResolver.update(
                    AlbumContract.StyleWallpaper.CONTENT_SELECTED_URI,
                    unselectedStyleValue, null, null)

            val unselectedVideoValue = ContentValues()
            unselectedVideoValue.put(AlbumContract.VideoWallpaper.COLUMN_NAME_SELECTED, 0)
            // unselected video old
            context.contentResolver.update(
                    AlbumContract.VideoWallpaper.CONTENT_SELECTED_URI,
                    unselectedVideoValue, null, null)

            var selectedCount = 0
            if (selectPreviewUri != null) {
                selectedCount = context.contentResolver.update(
                        selectPreviewUri, selectValue, null, null)
            }
            if (selectedCount > 0) {
                emitter.onNext(true)
            } else {
                emitter.onNext(false)
            }

            if (selectedCount > 0) {
                synchronized(caches) {
                    caches.forEach { it.makeDirty() }
                }

                notifyChange(context, AlbumContract.LiveWallpaper.CONTENT_SELECT_PREVIEWING_URI)
                notifyChange(context, AlbumContract.StyleWallpaper.CONTENT_SELECT_PREVIEWING_URI)
                notifyChange(context, AlbumContract.VideoWallpaper.CONTENT_SELECT_PREVIEWING_URI)
            }
            emitter.onComplete()
        }
    }

    override fun previewWallpaper(wallpaperId: String, type: WallpaperType): Observable<Boolean> {
        return Observable.create { emitter ->
            val previewingValue = ContentValues()
            previewingValue.put(COLUMN_NAME_WALLPAPER_TYPE,
                    type.typeInt)
            previewingValue.put(COLUMN_NAME_WALLPAPER_ID, wallpaperId)

            val uri = AlbumContract.PreviewingWallpaper.CONTENT_URI
            context.contentResolver.update(uri, previewingValue, null, null)

            val updateCount = context.contentResolver.update(uri, previewingValue, null, null)
            if (updateCount > 0) {
                emitter.onNext(true)
            } else {
                emitter.onNext(false)
            }

            emitter.onComplete()
        }
    }

    override fun activeService(serviceType: Int): Observable<Boolean> {
        return Observable.create { emitter ->
            if (serviceType != AlbumContract.ActiveService.SERVICE_NONE
                    && serviceType != AlbumContract.ActiveService.SERVICE_ORIGIN
                    && serviceType != AlbumContract.ActiveService.SERVICE_MIRROR) {
                emitter.onError(IllegalArgumentException("Service type illegal."))
                return@create
            }
            val uri = AlbumContract.ActiveService.CONTENT_URI
            val contentValue = ContentValues()
            contentValue.put(AlbumContract.ActiveService.COLUMN_NAME_SERVICE_ID, serviceType)
            val updateCount = context.contentResolver.update(uri, contentValue, null, null)
            emitter.onNext(updateCount > 0)
            emitter.onComplete()
        }
    }

    override fun getActiveService(): Observable<Int> {
        return Observable.create { emitter ->
            val uri = AlbumContract.ActiveService.CONTENT_URI
            var cursor: Cursor? = null
            var serviceType = AlbumContract.ActiveService.SERVICE_NONE
            try {
                cursor = context.contentResolver.query(uri, null, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    serviceType = cursor.getInt(
                            cursor.getColumnIndex(AlbumContract.ActiveService.COLUMN_NAME_SERVICE_ID))
                }
            } finally {
                cursor?.close()
            }
            emitter.onNext(serviceType)
            emitter.onComplete()
        }
    }

    private fun buildDefaultWallpaper(): WallpaperEntity {
        val entity = WallpaperEntity()
        entity.isDefault = true
        entity.id = -1
        entity.wallpaperId = LiveWallpaperDataStoreImpl.DEFAULT_WALLPAPER_ID
        entity.author = "Yalin"
        entity.link = "kinglloy.com"
        entity.name = "Rainbow"
        entity.type = WallpaperType.LIVE

        return entity
    }

    private fun filterDownloadedWallpapers(wallpapers: List<WallpaperEntity>)
            : ArrayList<WallpaperEntity> {
        val downloaded = ArrayList<WallpaperEntity>()
        wallpapers.filterNotTo(downloaded) {
            WallpaperFileHelper.isNeedDownloadWallpaper(it.lazyDownload,
                    it.storePath)
        }
        return downloaded
    }

}