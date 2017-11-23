package com.kinglloy.album.data.repository.datasource.io

import android.content.ContentProviderOperation
import android.content.Context
import android.database.Cursor
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.kinglloy.album.data.SyncConfig
import com.kinglloy.album.data.entity.TempVideoWallpaperEntity
import com.kinglloy.album.data.entity.WallpaperEntity
import com.kinglloy.album.data.entity.mapper.WallpaperEntityMapper
import com.kinglloy.album.data.exception.NetworkConnectionException
import com.kinglloy.album.data.exception.RemoteServerException
import com.kinglloy.album.data.log.LogUtil
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import com.kinglloy.album.data.repository.datasource.provider.AlbumContractHelper
import com.kinglloy.album.data.utils.WallpaperFileHelper
import io.reactivex.ObservableEmitter
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
class VideoWallpaperHandler(context: Context,
                            val mapper: WallpaperEntityMapper) : JSONHandler(context) {
    companion object {
        val TAG = "AdvanceWallpaperHandler"
        val downloadLock = ReentrantLock()
    }

    private var wallpapers: ArrayList<WallpaperEntity> = ArrayList()

    override fun makeContentProviderOperations(list: ArrayList<ContentProviderOperation>) {
        val uri = AlbumContractHelper.setUriAsCalledFromSyncAdapter(
                AlbumContract.VideoWallpaper.CONTENT_URI)
        list.add(ContentProviderOperation.newDelete(uri).build())

        val validFiles = HashSet<String>()
        val selectedEntities = querySelectedWallpapers()
        validFiles.addAll(getWallpaperNameSet(selectedEntities))
        for (wallpaper in this.wallpapers) {
            wallpaper.storePath = makeStorePath(wallpaper)
            if (!selectedEntities.contains(wallpaper)) {
                LogUtil.D(TAG, "download wallpaper component "
                        + " success, do output wallpaper.")
                outputWallpaper(wallpaper, list)
                validFiles.add(makeFilename(wallpaper))
            }
        }
        // delete old wallpapers
        WallpaperFileHelper.deleteOldVideoWallpaper(mContext, validFiles)
    }

    override fun process(element: JsonElement) {
        val tempWallpapers = Gson().fromJson(element, Array<TempVideoWallpaperEntity>::class.java)
        this.wallpapers.ensureCapacity(tempWallpapers.size)
        this.wallpapers.addAll(mapper.transformFromTempVideoEntity(tempWallpapers.toCollection(ArrayList())))
    }

    private fun makeFilename(wallpaper: WallpaperEntity): String {
        val suffix = getWallpaperFileSuffix(wallpaper.downloadUrl)
        return wallpaper.hashCode().toString() + "_video" + suffix
    }

    private fun getWallpaperFileSuffix(downloadUrl: String): String {
        val suffixStart = downloadUrl.lastIndexOf(".")
        val suffix: String
        suffix = if (suffixStart >= 0) {
            downloadUrl.substring(suffixStart)
        } else {
            ".mp4"
        }
        return suffix
    }

    private fun makeStorePath(wallpaper: WallpaperEntity): String {
        val outputDir = WallpaperFileHelper.getVideoWallpaperDir(mContext)
        return File(outputDir, makeFilename(wallpaper)).absolutePath
    }

    private fun getWallpaperNameSet(entities: List<WallpaperEntity>): Set<String> {
        val ids = HashSet<String>()
        for (entity in entities) {
            ids.add(makeFilename(entity))
        }
        return ids
    }

    private fun outputWallpaper(wallpaper: WallpaperEntity,
                                list: ArrayList<ContentProviderOperation>) {
        val uri = AlbumContractHelper.setUriAsCalledFromSyncAdapter(
                AlbumContract.VideoWallpaper.CONTENT_URI)
        val builder = ContentProviderOperation.newInsert(uri)
        builder.withValue(AlbumContract.VideoWallpaper.COLUMN_NAME_WALLPAPER_ID, wallpaper.wallpaperId)
        builder.withValue(AlbumContract.VideoWallpaper.COLUMN_NAME_DOWNLOAD_URL, wallpaper.downloadUrl)
        builder.withValue(AlbumContract.VideoWallpaper.COLUMN_NAME_ICON_URL, wallpaper.iconUrl)
        builder.withValue(AlbumContract.VideoWallpaper.COLUMN_NAME_NAME, wallpaper.name)
        builder.withValue(AlbumContract.VideoWallpaper.COLUMN_NAME_CHECKSUM, wallpaper.checkSum)
        builder.withValue(AlbumContract.VideoWallpaper.COLUMN_NAME_STORE_PATH, wallpaper.storePath)
        builder.withValue(AlbumContract.VideoWallpaper.COLUMN_NAME_SELECTED, 0)
        builder.withValue(AlbumContract.VideoWallpaper.COLUMN_NAME_PREVIEWING, 0)
        builder.withValue(AlbumContract.VideoWallpaper.COLUMN_NAME_SIZE, wallpaper.size)
        builder.withValue(AlbumContract.VideoWallpaper.COLUMN_NAME_PRO, wallpaper.pro)

        list.add(builder.build())
    }

    private fun querySelectedWallpapers(): List<WallpaperEntity> {
        var cursor: Cursor? = null
        try {
            cursor = mContext.contentResolver.query(
                    AlbumContract.VideoWallpaper.CONTENT_SELECTED_URI, null, null, null, null)
            return WallpaperEntity.videoWallpaperValues(cursor)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    private fun downloadVideoWallpaper(wallpaper: WallpaperEntity): Boolean {
        return downloadVideoWallpaper(wallpaper, null)
    }

    fun downloadVideoWallpaper(wallpaper: WallpaperEntity,
                               emitter: ObservableEmitter<Long>?): Boolean {
        emitter?.onNext(0)
        LogUtil.D(TAG, "Start download video wallpaper to " + wallpaper.storePath)
        val outputFile = File(wallpaper.storePath)
        if (outputFile.exists()) {
            if (WallpaperFileHelper.ensureChecksumValid(mContext,
                    wallpaper.checkSum, wallpaper.storePath)) {
                emitter?.onComplete()
                return true
            }
        }
        if (maybeCancelDownload(emitter)) {
            return false
        }
        synchronized(downloadLock) {
            var os: OutputStream? = null
            var _is: InputStream? = null
            try {
                if (outputFile.exists()) {
                    if (WallpaperFileHelper.ensureChecksumValid(mContext,
                            wallpaper.checkSum, wallpaper.storePath)) {
                        emitter?.onComplete()
                        return true
                    } else {
                        outputFile.delete()
                    }
                }

                val storePath = outputFile.parentFile
                storePath.mkdirs()
                os = FileOutputStream(outputFile)
                val httpClient = OkHttpClient.Builder()
                        .connectTimeout(SyncConfig.DEFAULT_CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                        .readTimeout(SyncConfig.DEFAULT_DOWNLOAD_TIMEOUT.toLong(), TimeUnit.SECONDS)
                        .build()
                val request = Request.Builder().url(URL(wallpaper.downloadUrl)).build()

                if (maybeCancelDownload(emitter)) {
                    return false
                }
                val response = httpClient.newCall(request).execute()
                val responseCode = response.code()
                if (responseCode in 200..299) {
                    _is = response.body().byteStream()
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    var writeLength = 0L
                    bytesRead = _is.read(buffer)
                    if (maybeCancelDownload(emitter)) {
                        return false
                    }
                    while (bytesRead > 0) {
                        os.write(buffer, 0, bytesRead)
                        writeLength += bytesRead
                        emitter?.onNext(writeLength)
                        bytesRead = _is.read(buffer)
                        if (maybeCancelDownload(emitter)) {
                            return false
                        }
                    }
                    os.flush()
                    emitter?.onComplete()
                    return true
                } else {
                    LogUtil.E(TAG, "Download wallpaper component " + wallpaper.name + " failed.")
                    emitter?.onError(RemoteServerException())
                    return false
                }
            } catch (e: IOException) {
                e.printStackTrace()
                emitter?.onError(NetworkConnectionException())
                LogUtil.E(TAG, "Download wallpaper component" + wallpaper.name + " failed.", e)
                return false
            } finally {
                ensureChecksum(outputFile, wallpaper.checkSum)
                try {
                    os?.close()
                    _is?.close()
                } catch (e: IOException) {
                    // ignore
                }
            }
        }
    }

    private fun maybeCancelDownload(emitter: ObservableEmitter<Long>?): Boolean {
        val canceled = emitter != null && emitter.isDisposed
        if (canceled) {
            LogUtil.D(TAG, "Download canceled...")
        }
        return canceled
    }

    private fun ensureChecksum(file: File, checkSum: String) {
        if (file.exists()) {
            if (!WallpaperFileHelper.ensureChecksumValid(mContext,
                    checkSum, file.absolutePath)) {
                file.delete()
            }
        }
    }

}