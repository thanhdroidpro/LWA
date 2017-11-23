package com.kinglloy.album.domain.interactor;


import com.kinglloy.album.domain.Wallpaper;
import com.kinglloy.album.domain.WallpaperType;
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

public class GetDownloadedWallpapers extends UseCase<List<Wallpaper>, Void> {

    private WallpaperRepository repository;

    @Inject
    public GetDownloadedWallpapers(ThreadExecutor threadExecutor,
                                   PostExecutionThread postExecutionThread,
                                   WallpaperRepository repository) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    public Observable<List<Wallpaper>> buildUseCaseObservable(Void params) {
        return repository.getDownloadedWallpapers();
    }
}
