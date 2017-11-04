package com.kinglloy.album.injection.modules

import android.content.Context
import com.kinglloy.album.UIThread
import com.kinglloy.album.data.cache.WallpaperCache
import com.kinglloy.album.data.cache.LiveWallpaperCacheImpl
import com.kinglloy.album.data.executor.JobExecutor
import com.kinglloy.album.data.repository.SettingsDataRepository
import com.kinglloy.album.data.repository.WallpaperDataRepository
import com.kinglloy.album.domain.executor.PostExecutionThread
import com.kinglloy.album.domain.executor.ThreadExecutor
import com.kinglloy.album.domain.repository.SettingsRepository
import com.kinglloy.album.domain.repository.WallpaperRepository

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

/**
 * @author jinyalin
 * *
 * @since 2017/4/18.
 */
@Module
class ApplicationModule(context: Context) {
    private val applicationContext: Context = context.applicationContext

    @Provides
    @Singleton
    internal fun provideApplicationContext(): Context {
        return applicationContext
    }

    @Provides
    @Singleton
    internal fun provideThreadExecutor(jobExecutor: JobExecutor): ThreadExecutor {
        return jobExecutor
    }

    @Provides
    @Singleton
    internal fun providePostExecutionThread(uiThread: UIThread): PostExecutionThread {
        return uiThread
    }


    @Provides
    @Singleton
    internal fun provideWallpaperRepository(repository: WallpaperDataRepository):
            WallpaperRepository {
        return repository
    }

    @Provides
    @Singleton
    internal fun provideSettingsRepository(repository: SettingsDataRepository):
            SettingsRepository {
        return repository
    }

}
