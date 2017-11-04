package com.kinglloy.album.domain.repository;

import com.kinglloy.album.domain.StyleWallpaperSettings;

import io.reactivex.Observable;

/**
 * @author jinyalin
 * @since 2017/11/4.
 */

public interface SettingsRepository {
    Observable<StyleWallpaperSettings> getStyleWallpaperSettings();

    Observable<Boolean> updateStyleWallpaperSettings(StyleWallpaperSettings newSettings);
}
