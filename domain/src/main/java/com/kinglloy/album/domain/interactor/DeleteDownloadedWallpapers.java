package com.kinglloy.album.domain.interactor;


import com.kinglloy.album.domain.executor.PostExecutionThread;
import com.kinglloy.album.domain.executor.ThreadExecutor;
import com.kinglloy.album.domain.repository.WallpaperRepository;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;


/**
 * @author jinyalin
 * @since 2017/7/28.
 */

public class DeleteDownloadedWallpapers extends
        UseCase<Boolean, DeleteDownloadedWallpapers.Params> {

    private WallpaperRepository repository;

    @Inject
    public DeleteDownloadedWallpapers(ThreadExecutor threadExecutor,
                                      PostExecutionThread postExecutionThread,
                                      WallpaperRepository repository) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Params params) {
        return repository.deleteDownloadedWallpapers(params.wallpapersPath);
    }

    public static final class Params {
        private final ArrayList<String> wallpapersPath;

        private Params(ArrayList<String> wallpapersPath) {
            this.wallpapersPath = wallpapersPath;
        }

        public static Params withPaths(ArrayList<String> wallpaperPaths) {
            return new Params(wallpaperPaths);
        }
    }
}
