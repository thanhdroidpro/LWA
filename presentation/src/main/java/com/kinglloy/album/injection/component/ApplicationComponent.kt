package com.kinglloy.album.injection.component

import android.content.Context
import com.kinglloy.album.AlbumApplication
import com.kinglloy.album.AlbumWallpaperService
import com.kinglloy.album.view.activity.WallpaperListActivity
import com.kinglloy.album.injection.modules.ApplicationModule

import javax.inject.Singleton

import dagger.Component

/**
 * @author jinyalin
 * *
 * @since 2017/4/18.
 */
@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
    fun inject(application: AlbumApplication)
    fun inject(mainActivity: WallpaperListActivity)

    fun inject(albumWallpaperService: AlbumWallpaperService)

    //Exposed to sub-graphs.
    fun context(): Context
}
