package com.kinglloy.album.domain.interactor.settings;

import com.kinglloy.album.domain.StyleWallpaperSettings;
import com.kinglloy.album.domain.WallpaperType;
import com.kinglloy.album.domain.executor.PostExecutionThread;
import com.kinglloy.album.domain.executor.ThreadExecutor;
import com.kinglloy.album.domain.interactor.DownloadWallpaper;
import com.kinglloy.album.domain.interactor.UseCase;
import com.kinglloy.album.domain.repository.SettingsRepository;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author jinyalin
 * @since 2017/11/4.
 */

public class UpdateStyleSettings extends UseCase<Boolean, UpdateStyleSettings.Params> {
    private SettingsRepository repository;

    @Inject
    public UpdateStyleSettings(ThreadExecutor threadExecutor,
                               PostExecutionThread postExecutionThread,
                               SettingsRepository repository) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(Params prams) {
        return repository.updateStyleWallpaperSettings(prams.settings);
    }

    public static final class Params {

        private final StyleWallpaperSettings settings;

        private Params(StyleWallpaperSettings settings) {
            this.settings = settings;
        }

        public static Params with(StyleWallpaperSettings settings) {
            return new Params(settings);
        }
    }
}
