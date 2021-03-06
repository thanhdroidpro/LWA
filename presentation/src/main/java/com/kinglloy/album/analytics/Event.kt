package com.kinglloy.album.analytics


/**
 * @author jinyalin
 * *
 * @since 2017/5/3.
 */

object Event {
    val WALLPAPER_CREATED = "wallpaper_created"
    val WALLPAPER_DESTROYED = "wallpaper_destroyed"
    val DEVICE_UNSUPPORTED = "device_unsupported"

    val LOAD_ADVANCES = "load_advance_wallpapers"
    val RETRY_LOAD_ADVANCES = "retry_load_advance_wallpapers"
    val DOWNLOAD_COMPONENT = "download_component"

    val OPEN_AD_ACTIVITY = "open_ad_activity"
    val TODAY_NOT_SHOW = "today_not_show"
    val WATCH_AD_AGAIN = "watch_ad_again"
    val LOAD_INTER_AD_FAILED = "load_inter_ad_failed"
    val LOAD_VIDEO_AD_FAILED = "load_video_ad_failed"
    val OPEN_INTER_AD = "open_inter_ad"
    val OPEN_VIDEO_AD = "open_video_ad"

    val CHANNEL_KEY = "channel"
}
