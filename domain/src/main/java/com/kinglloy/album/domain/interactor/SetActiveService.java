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

public class SetActiveService extends UseCase<Boolean, SetActiveService.Params> {
    private WallpaperRepository repository;

    @Inject
    public SetActiveService(ThreadExecutor threadExecutor,
                            PostExecutionThread postExecutionThread,
                            WallpaperRepository repository) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(SetActiveService.Params params) {
        return repository.activeService(params.serviceType);
    }

    public static final class Params {
        private final int serviceType;

        private Params(int serviceType) {
            this.serviceType = serviceType;
        }

        public static SetActiveService.Params setActiveService(int serviceType) {
            return new SetActiveService.Params(serviceType);
        }
    }
}