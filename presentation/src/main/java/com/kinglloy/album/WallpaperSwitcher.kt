package com.kinglloy.album

import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.kinglloy.album.analytics.Analytics
import com.kinglloy.album.analytics.Event
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import com.kinglloy.album.domain.interactor.DefaultObserver
import com.kinglloy.album.domain.interactor.GetActiveService
import org.jetbrains.anko.toast
import javax.inject.Inject

/**
 * @author jinyalin
 * @since 2017/9/27.
 */
class WallpaperSwitcher @Inject constructor(val getActiveService: GetActiveService) {
    fun switchService(context: Context) {
        getActiveService.execute(object : DefaultObserver<Int>() {
            override fun onNext(serviceType: Int) {
                if (serviceType == AlbumContract.ActiveService.SERVICE_NONE
                        || serviceType == AlbumContract.ActiveService.SERVICE_MIRROR) {
                    pickWallpaper(context, AlbumWallpaperService::class.java)
                } else {
                    pickWallpaper(context, AlbumWallpaperServiceMirror::class.java)
                }
            }
        }, null)
    }

    companion object {
        fun pickWallpaper(context: Context, targetClass: Class<*>) {
            try {
                context.startActivity(Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                        .putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                ComponentName(context, targetClass))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } catch (e: ActivityNotFoundException) {
                try {
                    context.startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } catch (e2: ActivityNotFoundException) {
                    context.toast(R.string.exception_message_device_unsupported)
                    Analytics.logEvent(context, Event.DEVICE_UNSUPPORTED)
                }
            }
        }
    }
}