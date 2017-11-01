package com.kinglloy.album.domain.interactor;


import com.kinglloy.album.domain.WallpaperType;
import com.kinglloy.album.domain.executor.PostExecutionThread;
import com.kinglloy.album.domain.executor.ThreadExecutor;
import com.kinglloy.album.domain.repository.WallpaperRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author jinyalin
 * @since 2017/7/31.
 */

public class PreviewWallpaper extends UseCase<Boolean, PreviewWallpaper.Params> {
    private WallpaperRepository repository;

    @Inject
    public PreviewWallpaper(ThreadExecutor threadExecutor,
                            PostExecutionThread postExecutionThread,
                            WallpaperRepository repository) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    Observable<Boolean> buildUseCaseObservable(PreviewWallpaper.Params params) {
        return repository.previewWallpaper(params.wallpaperId, params.wallpaperType);

    }

    public static final class Params {
        private final String wallpaperId;
        private final WallpaperType wallpaperType;

        private Params(String wallpaperId, WallpaperType wallpaperType) {
            this.wallpaperId = wallpaperId;
            this.wallpaperType = wallpaperType;
        }

        public static Params previewWallpaper(String wallpaperId, WallpaperType wallpaperType) {
            return new Params(wallpaperId, wallpaperType);
        }
    }
}