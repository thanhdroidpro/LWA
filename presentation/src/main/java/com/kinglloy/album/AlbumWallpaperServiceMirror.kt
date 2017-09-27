package com.kinglloy.album

import com.kinglloy.album.data.repository.datasource.provider.AlbumContract

/**
 * YaLin 2016/12/30.
 */
class AlbumWallpaperServiceMirror : AlbumWallpaperService() {

    override fun getActiveState(): Int = AlbumContract.ActiveService.SERVICE_MIRROR
}
