package com.kinglloy.album.domain.interactor;

import com.kinglloy.album.domain.Wallpaper;
import com.kinglloy.album.domain.repository.WallpaperRepository;

import javax.inject.Inject;

/**
 * @author jinyalin
 * @since 2017/7/28.
 */

public class GetPreviewWallpaper {
    private WallpaperRepository repository;

    @Inject
    public GetPreviewWallpaper(WallpaperRepository repository) {
        this.repository = repository;
    }

    public Wallpaper getPreviewing() {
        return repository.getPreviewingWallpaper();
    }
}
