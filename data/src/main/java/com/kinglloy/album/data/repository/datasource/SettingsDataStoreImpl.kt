package com.kinglloy.album.data.repository.datasource

import android.content.ContentValues
import android.content.Context
import com.kinglloy.album.data.entity.StyleWallpaperSettingsEntity
import com.kinglloy.album.data.repository.datasource.provider.settings.SettingsContract
import com.kinglloy.album.data.repository.datasource.provider.settings.SettingsContract
        .StyleWallpaperSettingsColumns
import io.reactivex.Observable

/**
 * @author jinyalin
 * @since 2017/11/4.
 */
class SettingsDataStoreImpl(private val context: Context) : SettingsDataStore {
    override fun updateStyleWallpaperSettings(settings: StyleWallpaperSettingsEntity)
            : Observable<Boolean> {
        return Observable.create { emitter ->
            val uri = SettingsContract.StyleWallpaperSettings.CONTENT_URI
            val values = ContentValues()
            values.put(StyleWallpaperSettingsColumns.COLUMN_NAME_ENABLE_EFFECT, settings.enableEffect)
            values.put(StyleWallpaperSettingsColumns.COLUMN_NAME_BLUR, settings.blur)
            values.put(StyleWallpaperSettingsColumns.COLUMN_NAME_DIM, settings.dim)
            values.put(StyleWallpaperSettingsColumns.COLUMN_NAME_GREY, settings.grey)
            context.contentResolver.update(uri, values, null, null)

            emitter.onNext(true)
            emitter.onComplete()
        }
    }

    override fun getStyleWallpaperSettings(): Observable<StyleWallpaperSettingsEntity> {
        return Observable.create { emitter ->
            val uri = SettingsContract.StyleWallpaperSettings.CONTENT_URI
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            val entity = StyleWallpaperSettingsEntity()
            try {
                if (cursor != null && cursor.moveToFirst()) {

                    entity.enableEffect = cursor.getInt(cursor.getColumnIndex(
                            StyleWallpaperSettingsColumns.COLUMN_NAME_ENABLE_EFFECT)) == 1
                    entity.blur = cursor.getInt(cursor.getColumnIndex(
                            StyleWallpaperSettingsColumns.COLUMN_NAME_BLUR))
                    entity.dim = cursor.getInt(cursor.getColumnIndex(
                            StyleWallpaperSettingsColumns.COLUMN_NAME_DIM))
                    entity.grey = cursor.getInt(cursor.getColumnIndex(
                            StyleWallpaperSettingsColumns.COLUMN_NAME_GREY))
                }
            } finally {
                cursor?.close()
            }
            emitter.onNext(entity)
            emitter.onComplete()
        }
    }

}