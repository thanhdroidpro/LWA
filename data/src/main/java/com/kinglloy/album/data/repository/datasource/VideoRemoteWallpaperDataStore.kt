package com.kinglloy.album.data.repository.datasource

import android.content.Context
import com.kinglloy.album.data.entity.WallpaperEntity
import com.kinglloy.album.data.exception.RemoteServerException
import com.kinglloy.album.domain.WallpaperType
import io.reactivex.Observable

/**
 * @author jinyalin
 * @since 2017/10/31.
 */
class VideoRemoteWallpaperDataStore(val context: Context,
                                    private val localDataStoreStyle: VideoWallpaperDataStoreImpl)
    : WallpaperDataStore {

    companion object {
        val TAG = "StyleRemoteWallpaperDataStore"
    }

    override fun getPreviewWallpaperEntity(): WallpaperEntity {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getWallpaperEntities(): Observable<List<WallpaperEntity>> {
        return Observable.create { emitter ->
            emitter.onError(RemoteServerException())
            emitter.onComplete()
        }
    }

    override fun selectPreviewingWallpaper(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun previewWallpaper(wallpaperId: String, type: WallpaperType): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelPreviewing(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelSelect(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun downloadWallpaper(wallpaperId: String): Observable<Long> {
        return Observable.create { emitter ->
            emitter.onNext(0)
            emitter.onComplete()
        }
    }

    override fun activeService(serviceType: Int): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActiveService(): Observable<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}