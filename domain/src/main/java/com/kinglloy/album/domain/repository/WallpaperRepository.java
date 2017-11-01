package com.kinglloy.album.domain.repository;

import com.kinglloy.album.domain.Wallpaper;
import com.kinglloy.album.domain.WallpaperType;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author jinyalin
 * @since 2017/9/26.
 */

public interface WallpaperRepository {

    Observable<List<Wallpaper>> getLiveWallpapers();

    Observable<List<Wallpaper>> getStyleWallpapers();

    Observable<List<Wallpaper>> getVideoWallpapers();

    Observable<List<Wallpaper>> loadLiveWallpapers();

    Observable<List<Wallpaper>> loadStyleWallpapers();

    Observable<List<Wallpaper>> loadVideoWallpapers();

    Observable<Long> downloadLiveWallpaper(String wallpaperId);

    Observable<Long> downloadStyleWallpaper(String wallpaperId);

    Observable<Long> downloadVideoWallpaper(String wallpaperId);

    Observable<Boolean> selectPreviewingWallpaper();

    Observable<Boolean> previewWallpaper(String wallpaperId, WallpaperType type);

    Wallpaper getPreviewingWallpaper();

    Observable<Boolean> activeService(int serviceType);

    Observable<Integer> getActiveService();
}
