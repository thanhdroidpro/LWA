package com.kinglloy.album.domain.interactor.settings;

import com.kinglloy.album.domain.StyleWallpaperSettings;
import com.kinglloy.album.domain.executor.PostExecutionThread;
import com.kinglloy.album.domain.executor.ThreadExecutor;
import com.kinglloy.album.domain.interactor.UseCase;
import com.kinglloy.album.domain.repository.SettingsRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author jinyalin
 * @since 2017/11/4.
 */

public class GetStyleWallpaperSettings extends UseCase<StyleWallpaperSettings, Void> {
    private SettingsRepository repository;

    @Inject
    public GetStyleWallpaperSettings(ThreadExecutor threadExecutor,
                                     PostExecutionThread postExecutionThread,
                                     SettingsRepository repository) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    public Observable<StyleWallpaperSettings> buildUseCaseObservable(Void aVoid) {
        return repository.getStyleWallpaperSettings();
    }
}
