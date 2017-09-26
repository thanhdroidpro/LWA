package com.kinglloy.album.domain.interactor;


import com.kinglloy.album.domain.executor.PostExecutionThread;
import com.kinglloy.album.domain.executor.ThreadExecutor;
import com.kinglloy.album.domain.repository.WallpaperRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author jinyalin
 * @since 2017/7/31.
 */

public class PreviewAdvanceWallpaper extends UseCase<Boolean, PreviewAdvanceWallpaper.Params> {
    private WallpaperRepository repository;

    @Inject
    public PreviewAdvanceWallpaper(ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread,
                                   WallpaperRepository repository) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    Observable<Boolean> buildUseCaseObservable(PreviewAdvanceWallpaper.Params params) {
        return repository.previewAdvanceWallpaper(params.wallpaperId);
    }

    public static final class Params {
        private final String wallpaperId;

        private Params(String wallpaperId) {
            this.wallpaperId = wallpaperId;
        }

        public static Params previewWallpaper(String wallpaperId) {
            return new Params(wallpaperId);
        }
    }
}