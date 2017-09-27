package com.kinglloy.album.domain.interactor;

import com.kinglloy.album.domain.executor.PostExecutionThread;
import com.kinglloy.album.domain.executor.ThreadExecutor;
import com.kinglloy.album.domain.repository.WallpaperRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author jinyalin
 * @since 2017/9/27.
 */

public class GetActiveService extends UseCase<Integer, Void> {
    private WallpaperRepository repository;

    @Inject
    public GetActiveService(ThreadExecutor threadExecutor,
                                PostExecutionThread postExecutionThread,
                                WallpaperRepository repository) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    Observable<Integer> buildUseCaseObservable(Void aVoid) {
        return repository.getActiveService();
    }
}
