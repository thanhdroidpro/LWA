package com.kinglloy.album.data.repository.datasource

import com.kinglloy.album.data.entity.StyleWallpaperSettingsEntity
import io.reactivex.Observable

/**
 * @author jinyalin
 * @since 2017/11/4.
 */
interface SettingsDataStore {
    fun updateStyleWallpaperSettings(settings: StyleWallpaperSettingsEntity)
            : Observable<Boolean>

    fun getStyleWallpaperSettings(): Observable<StyleWallpaperSettingsEntity>
}