package com.kinglloy.album.injection.modules

import android.content.Context
import com.kinglloy.album.UIThread
import com.kinglloy.album.data.cache.WallpaperCache
import com.kinglloy.album.data.cache.LiveWallpaperCacheImpl
import com.kinglloy.album.data.executor.JobExecutor
import com.kinglloy.album.data.repository.AdvanceWallpaperDataRepository
import com.kinglloy.album.domain.executor.PostExecutionThread
import com.kinglloy.album.domain.executor.ThreadExecutor
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
    internal fun provideWallpaperRepository(repository: AdvanceWallpaperDataRepository):
            WallpaperRepository {
        return repository
    }

    @Provides
    @Singleton
    internal fun provideAdvanceWallpaperCache(cacheLive: LiveWallpaperCacheImpl):
            WallpaperCache {
        return cacheLive
    }

}
