package com.kinglloy.album.mapper

import com.kinglloy.album.domain.StyleWallpaperSettings
import com.kinglloy.album.model.StyleSettingsItem

/**
 * @author jinyalin
 * @since 2017/11/4.
 */

object SettingsWallpaperItemMapper {
    fun transformStyleSettings(styleWallpaperSettings: StyleWallpaperSettings)
            : StyleSettingsItem {
        val entity = StyleSettingsItem()
        entity.enableEffect = styleWallpaperSettings.enableEffect
        entity.blur = styleWallpaperSettings.blur
        entity.dim = styleWallpaperSettings.dim
        entity.grey = styleWallpaperSettings.grey
        return entity
    }

    fun transToStyleSettings(styleSettingsItem: StyleSettingsItem)
            : StyleWallpaperSettings {
        val entity = StyleWallpaperSettings()
        entity.enableEffect = styleSettingsItem.enableEffect
        entity.blur = styleSettingsItem.blur
        entity.dim = styleSettingsItem.dim
        entity.grey = styleSettingsItem.grey
        return entity
    }
}