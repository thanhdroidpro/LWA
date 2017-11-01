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

public class SelectPreviewingAdvanceWallpaper extends UseCase<Boolean, Void> {
    private WallpaperRepository repository;

    @Inject
    public SelectPreviewingAdvanceWallpaper(ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread,
                                  WallpaperRepository repository) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    Observable<Boolean> buildUseCaseObservable(Void aVoid) {
        return repository.selectPreviewingWallpaper();
    }
}