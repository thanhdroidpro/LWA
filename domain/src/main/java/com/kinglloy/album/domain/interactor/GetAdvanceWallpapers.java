package com.kinglloy.album.domain.interactor;


import com.kinglloy.album.domain.AdvanceWallpaper;
import com.kinglloy.album.domain.executor.PostExecutionThread;
import com.kinglloy.album.domain.executor.ThreadExecutor;
import com.kinglloy.album.domain.repository.WallpaperRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;


/**
 * @author jinyalin
 * @since 2017/7/28.
 */

public class GetAdvanceWallpapers extends UseCase<List<AdvanceWallpaper>, Void> {
    private WallpaperRepository repository;

    @Inject
    public GetAdvanceWallpapers(ThreadExecutor threadExecutor,
                                PostExecutionThread postExecutionThread,
                                WallpaperRepository repository) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    Observable<List<AdvanceWallpaper>> buildUseCaseObservable(Void aVoid) {
        return repository.getAdvanceWallpapers();
    }
}
