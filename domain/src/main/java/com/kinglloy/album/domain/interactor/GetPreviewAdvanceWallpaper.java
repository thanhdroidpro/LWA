package com.kinglloy.album.domain.interactor;

import com.kinglloy.album.domain.AdvanceWallpaper;
import com.kinglloy.album.domain.repository.WallpaperRepository;

import javax.inject.Inject;

/**
 * @author jinyalin
 * @since 2017/7/28.
 */

public class GetPreviewAdvanceWallpaper {
    private WallpaperRepository repository;

    @Inject
    public GetPreviewAdvanceWallpaper(WallpaperRepository repository) {
        this.repository = repository;
    }

    public AdvanceWallpaper getPreviewing() {
        return repository.getPreviewAdvanceWallpaper();
    }
}
