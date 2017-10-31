package com.kinglloy.album.data.repository.datasource

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import io.reactivex.Observable

/**
 * @author jinyalin
 * @since 2017/10/31.
 */
abstract class BaseWallpaperDataStore(val context: Context) : WallpaperDataStore {
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
}