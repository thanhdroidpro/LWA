package com.kinglloy.album.domain.interactor;


import com.kinglloy.album.domain.WallpaperType;
import com.kinglloy.album.domain.executor.PostExecutionThread;
import com.kinglloy.album.domain.executor.ThreadExecutor;
import com.kinglloy.album.domain.repository.WallpaperRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author jinyalin
 * @since 2017/8/11.
 */

public class DownloadWallpaper extends UseCase<Long, DownloadWallpaper.Params> {
    private WallpaperRepository repository;

    @Inject
    public DownloadWallpaper(ThreadExecutor threadExecutor,
                             PostExecutionThread postExecutionThread,
                             WallpaperRepository repository) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    Observable<Long> buildUseCaseObservable(Params params) {
        if (params.wallpaperType == WallpaperType.LIVE) {
            return repository.downloadLiveWallpaper(params.wallpaperId);
        } else if (params.wallpaperType == WallpaperType.STYLE) {
            return repository.downloadStyleWallpaper(params.wallpaperId);
        } else {
            return repository.downloadVideoWallpaper(params.wallpaperId);
        }
    }

    public static final class Params {

        private final String wallpaperId;
        private final WallpaperType wallpaperType;

        private Params(String wallpaperId, WallpaperType wallpaperType) {
            this.wallpaperId = wallpaperId;
            this.wallpaperType = wallpaperType;
        }

        public static Params download(String wallpaperId, WallpaperType wallpaperType) {
            return new Params(wallpaperId, wallpaperType);
        }
    }
}