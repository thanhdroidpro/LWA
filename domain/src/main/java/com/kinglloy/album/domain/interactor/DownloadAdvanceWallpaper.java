package com.kinglloy.album.domain.interactor;


import com.kinglloy.album.domain.executor.PostExecutionThread;
import com.kinglloy.album.domain.executor.ThreadExecutor;
import com.kinglloy.album.domain.repository.WallpaperRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author jinyalin
 * @since 2017/8/11.
 */

public class DownloadAdvanceWallpaper extends UseCase<Long, DownloadAdvanceWallpaper.Params> {
    private WallpaperRepository repository;

    @Inject
    public DownloadAdvanceWallpaper(ThreadExecutor threadExecutor,
                                    PostExecutionThread postExecutionThread,
                                    WallpaperRepository repository) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    Observable<Long> buildUseCaseObservable(Params params) {
        return repository.downloadAdvanceWallpaper(params.wallpaperId);
    }

    public static final class Params {

        private final String wallpaperId;

        private Params(String wallpaperId) {
            this.wallpaperId = wallpaperId;
        }

        public static Params download(String wallpaperId) {
            return new Params(wallpaperId);
        }
    }
}