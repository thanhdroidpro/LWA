package com.kinglloy.album.domain.repository;

import com.kinglloy.album.domain.AdvanceWallpaper;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author jinyalin
 * @since 2017/9/26.
 */

public interface WallpaperRepository {

    Observable<List<AdvanceWallpaper>> getAdvanceWallpapers();

    Observable<List<AdvanceWallpaper>> loadAdvanceWallpapers();

    Observable<Long> downloadAdvanceWallpaper(String wallpaperId);

    Observable<Boolean> selectPreviewingAdvanceWallpaper();

    Observable<Boolean> previewAdvanceWallpaper(String wallpaperId);

    AdvanceWallpaper getPreviewAdvanceWallpaper();

    Observable<Boolean> activeService(int serviceType);
    Observable<Integer> getActiveService();
}
