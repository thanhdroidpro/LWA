package com.kinglloy.album.data.repository

import android.content.Context
import com.kinglloy.album.data.entity.mapper.SettingsWallpaperEntityMapper
import com.kinglloy.album.data.repository.datasource.SettingsDataStoreFactory
import com.kinglloy.album.domain.StyleWallpaperSettings
import com.kinglloy.album.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author jinyalin
 * @since 2017/11/4.
 */
@Singleton
class SettingsDataRepository
@Inject constructor(val context: Context,
                    val factory: SettingsDataStoreFactory) : SettingsRepository {
    override fun getStyleWallpaperSettings() =
            factory.createSettingsDataStore().getStyleWallpaperSettings()
                    .map(SettingsWallpaperEntityMapper::transformStyleSettingsEntity)!!

    override fun updateStyleWallpaperSettings(newSettings: StyleWallpaperSettings) =
            factory.createSettingsDataStore().updateStyleWallpaperSettings(
                    SettingsWallpaperEntityMapper.transToStyleSettingsEntity(newSettings))
}