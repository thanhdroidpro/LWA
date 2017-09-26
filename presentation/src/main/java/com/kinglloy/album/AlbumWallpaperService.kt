package com.kinglloy.album

import android.service.wallpaper.WallpaperService
import com.kinglloy.album.domain.interactor.DefaultObserver
import com.kinglloy.album.domain.interactor.SelectPreviewingAdvanceWallpaper
import com.kinglloy.album.engine.ProxyProvider
import com.kinglloy.album.engine.WallpaperActiveCallback

import javax.inject.Inject


/**
 * YaLin 2016/12/30.
 */
open class AlbumWallpaperService :
        WallpaperService(), WallpaperActiveCallback {
    @Inject lateinit var proxyProvider: ProxyProvider
    @Inject lateinit var selectAdvanceWallpaper: SelectPreviewingAdvanceWallpaper

    private var proxy: WallpaperService? = null

    init {
        AlbumApplication.instance.applicationComponent.inject(this)
    }

    override fun onCreateEngine(): WallpaperService.Engine {
        return proxy!!.onCreateEngine()
    }

    override fun onCreate() {
        super.onCreate()
        proxy = proxyProvider.provideProxy(this)
        proxy?.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        proxy?.onDestroy()
    }

    override fun onWallpaperActivate() {
        setActiveState(this, getActiveState())
        selectAdvanceWallpaper.execute(object : DefaultObserver<Boolean>() {}, null)
//        Analytics.logEvent(this, Event.WALLPAPER_CREATED)
//        EventBus.getDefault().postSticky(WallpaperActivateEvent(true))
    }

    override fun onWallpaperDeactivate() {
        setActiveState(this, ACTIVE_NONE)
//        Analytics.logEvent(this, Event.WALLPAPER_DESTROYED)
//        EventBus.getDefault().postSticky(WallpaperActivateEvent(false))
    }

    open fun getActiveState(): Int {
        return ACTIVE_ORIGINE
    }
}
