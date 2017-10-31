package com.kinglloy.album.data.repository.datasource

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.Context
import android.content.OperationApplicationException
import android.database.Cursor
import android.os.RemoteException
import com.google.gson.JsonParser
import com.kinglloy.album.data.R
import com.kinglloy.album.data.entity.WallpaperEntity
import com.kinglloy.album.data.exception.NetworkConnectionException
import com.kinglloy.album.data.exception.NoContentException
import com.kinglloy.album.data.exception.RemoteServerException
import com.kinglloy.album.data.log.LogUtil
import com.kinglloy.album.data.repository.datasource.io.StyleWallpaperHandler
import com.kinglloy.album.data.repository.datasource.net.RemoteStyleWallpaperFetcher
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import com.kinglloy.album.data.repository.datasource.sync.SyncHelper
import com.kinglloy.album.data.repository.datasource.sync.account.Account
import io.reactivex.Observable

/**
 * @author jinyalin
 * @since 2017/10/31.
 */
class StyleRemoteWallpaperDataStore(val context: Context,
                                    val localDataStoreStyle: StyleWallpaperDataStoreImpl)
    : WallpaperDataStore {

    companion object {
        val TAG = "StyleRemoteWallpaperDataStore"
    }

    override fun getPreviewWallpaperEntity(): WallpaperEntity {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getWallpaperEntities(): Observable<List<WallpaperEntity>> {
        return Observable.create { emitter ->
            if (!SyncHelper.isOnline(context)) {
                emitter.onError(NetworkConnectionException())
                return@create
            }
            val account = Account.getAccount()
            val authority = context.getString(R.string.authority)
            ContentResolver.cancelSync(account, authority)

            val batch = ArrayList<ContentProviderOperation>()
            try {
                val wallpapers = RemoteStyleWallpaperFetcher(context).fetchDataIfNewer()
                val parser = JsonParser()
                val handler = StyleWallpaperHandler(context)
                handler.process(parser.parse(wallpapers))
                handler.makeContentProviderOperations(batch)
            } catch (e: Exception) {
                emitter.onError(RemoteServerException())
                return@create
            }

            try {
                val operations = batch.size
                if (operations > 0) {
                    context.contentResolver.applyBatch(AlbumContract.AUTHORITY, batch)
                }
            } catch (ex: RemoteException) {
                LogUtil.D(TAG, "RemoteException while applying content provider operations.")
                throw RuntimeException("Error executing content provider batch operation", ex)
            } catch (ex: OperationApplicationException) {
                LogUtil.D(TAG, "OperationApplicationException while applying content provider operations.")
                throw RuntimeException("Error executing content provider batch operation", ex)
            }

            var cursor: Cursor? = null
            val validWallpapers = ArrayList<WallpaperEntity>()
            try {
                val contentResolver = context.contentResolver
                cursor = contentResolver.query(AlbumContract.StyleWallpaper.CONTENT_URI,
                        null, null, null, null)
                validWallpapers.addAll(WallpaperEntity.styleWallpaperValues(cursor))
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }

            if (validWallpapers.isEmpty()) {
                emitter.onError(NoContentException())
            } else {
                emitter.onNext(validWallpapers)
            }
            emitter.onComplete()
        }
    }

    override fun selectPreviewingWallpaper(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun previewWallpaper(wallpaperId: String): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelPreviewing(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelSelect(): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun downloadWallpaper(wallpaperId: String): Observable<Long> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun activeService(serviceType: Int): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActiveService(): Observable<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}