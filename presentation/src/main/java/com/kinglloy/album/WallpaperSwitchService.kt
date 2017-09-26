package com.kinglloy.album

import android.app.Service
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.kinglloy.album.analytics.Analytics
import com.kinglloy.album.analytics.Event
import org.jetbrains.anko.toast

/**
 * @author jinyalin
 * @since 2017/9/26.
 */
class WallpaperSwitchService : Service() {

    override fun onBind(p0: Intent?): IBinder {
        return WallpaperSwitch()
    }

    inner class WallpaperSwitch : IWallpaperSwitch.Stub() {
        override fun switchWallpaper() {
            val activeState = currentActiveState(this@WallpaperSwitchService)
            if (activeState == ACTIVE_NONE || activeState == ACTIVE_MIRROR) {
                pickWallpaper(this@WallpaperSwitchService, AlbumWallpaperService::class.java)
            } else {
                pickWallpaper(this@WallpaperSwitchService, AlbumWallpaperServiceMirror::class.java)
            }
        }
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