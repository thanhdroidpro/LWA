package com.kinglloy.album.data.repository.datasource.provider.settings

import android.net.Uri
import com.kinglloy.album.data.BuildConfig

/**
 * @author jinyalin
 * @since 2017/11/4.
 */
object SettingsContract {
    val AUTHORITY = BuildConfig.SETTINGS_AUTHORITY

    private val SCHEME = "content://"

    val BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY)!!

    private val PATH_STYLE_WALLPAPER_SETTINGS = "style_wallpaper_settings"

    interface StyleWallpaperSettingsColumns {
        companion object {
            /**
             * Type: Boolean
             */
            val COLUMN_NAME_ENABLE_EFFECT = "enable_effect"
            /**
             * Type: INTEGER
             */
            val COLUMN_NAME_BLUR = "blur"
            /**
             * Type: INTEGER
             */
            val COLUMN_NAME_DIM = "dim"
            /**
             * Type: INTEGER
             */
            val COLUMN_NAME_GREY = "grey"
        }
    }

    class StyleWallpaperSettings : StyleWallpaperSettingsColumns {
        companion object {
            val SP_NAME = "style_settings"
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                    .appendEncodedPath(PATH_STYLE_WALLPAPER_SETTINGS).build()!!
        }

    }
}