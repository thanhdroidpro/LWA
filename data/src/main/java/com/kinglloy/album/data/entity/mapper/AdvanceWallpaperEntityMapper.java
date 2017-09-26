package com.kinglloy.album.data.entity.mapper;

import com.fernandocejas.arrow.checks.Preconditions;
import com.kinglloy.album.data.entity.AdvanceWallpaperEntity;
import com.kinglloy.album.domain.AdvanceWallpaper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
@Singleton
public class AdvanceWallpaperEntityMapper {
    @Inject
    public AdvanceWallpaperEntityMapper() {

    }

    public AdvanceWallpaper transform(AdvanceWallpaperEntity wallpaperEntity) {
        Preconditions.checkNotNull(wallpaperEntity, "Wallpaper can not be null.");
        AdvanceWallpaper wallpaper = new AdvanceWallpaper();
        wallpaper.id = wallpaperEntity.id;
        wallpaper.wallpaperId = wallpaperEntity.wallpaperId;
        wallpaper.link = wallpaperEntity.link;
        wallpaper.name = wallpaperEntity.name;
        wallpaper.author = wallpaperEntity.author;
        wallpaper.iconUrl = wallpaperEntity.iconUrl;
        wallpaper.downloadUrl = wallpaperEntity.downloadUrl;
        wallpaper.providerName = wallpaperEntity.providerName;
        wallpaper.storePath = wallpaperEntity.storePath;
        wallpaper.isDefault = wallpaperEntity.isDefault;
        wallpaper.isSelected = wallpaperEntity.isSelected;
        wallpaper.lazyDownload = wallpaperEntity.lazyDownload;

        return wallpaper;
    }

    public List<AdvanceWallpaper> transformList(List<AdvanceWallpaperEntity> wallpaperEntities) {
        Preconditions.checkNotNull(wallpaperEntities, "SourceEntity can not be null.");
        List<AdvanceWallpaper> sources = new ArrayList<>();
        for (AdvanceWallpaperEntity entity : wallpaperEntities) {
            sources.add(transform(entity));
        }
        return sources;
    }
}
