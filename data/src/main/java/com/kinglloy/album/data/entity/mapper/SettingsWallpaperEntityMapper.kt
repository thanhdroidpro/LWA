package com.kinglloy.album.data.entity.mapper

import com.kinglloy.album.data.entity.StyleWallpaperSettingsEntity
import com.kinglloy.album.domain.StyleWallpaperSettings

/**
 * @author jinyalin
 * @since 2017/11/4.
 */

object SettingsWallpaperEntityMapper {
    fun transformStyleSettingsEntity(styleWallpaperSettings: StyleWallpaperSettingsEntity)
            : StyleWallpaperSettings {
        val entity = StyleWallpaperSettings()
        entity.enableEffect = styleWallpaperSettings.enableEffect
        entity.blur = styleWallpaperSettings.blur
        entity.dim = styleWallpaperSettings.dim
        entity.grey = styleWallpaperSettings.grey
        return entity
    }

    fun transToStyleSettingsEntity(styleWallpaperSettings: StyleWallpaperSettings)
            : StyleWallpaperSettingsEntity {
        val entity = StyleWallpaperSettingsEntity()
        entity.enableEffect = styleWallpaperSettings.enableEffect
        entity.blur = styleWallpaperSettings.blur
        entity.dim = styleWallpaperSettings.dim
        entity.grey = styleWallpaperSettings.grey
        return entity
    }
}