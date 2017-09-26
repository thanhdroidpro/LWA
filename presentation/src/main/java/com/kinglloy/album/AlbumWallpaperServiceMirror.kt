package com.kinglloy.album

/**
 * YaLin 2016/12/30.
 */
class AlbumWallpaperServiceMirror : AlbumWallpaperService() {

    override fun getActiveState(): Int {
        return ACTIVE_MIRROR
    }
}
