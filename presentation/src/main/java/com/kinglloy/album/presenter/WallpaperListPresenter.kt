package com.kinglloy.album.presenter

import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import com.kinglloy.album.WallpaperSwitcher
import com.kinglloy.album.data.exception.NetworkConnectionException
import com.kinglloy.album.data.log.LogUtil
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import com.kinglloy.album.data.utils.WallpaperFileHelper
import com.kinglloy.album.domain.Wallpaper
import com.kinglloy.album.domain.WallpaperType
import com.kinglloy.album.domain.interactor.*
import com.kinglloy.album.exception.ErrorMessageFactory
import com.kinglloy.album.mapper.AdvanceWallpaperItemMapper
import com.kinglloy.album.model.WallpaperItem
import com.kinglloy.album.view.WallpaperListView
import com.kinglloy.download.DownloadListener
import com.kinglloy.download.KinglloyDownloader
import com.kinglloy.download.exceptions.ErrorCode
import com.kinglloy.download.state.DownloadState
import javax.inject.Inject

/**
 * @author jinyalin
 * @since 2017/10/31.
 */
class WallpaperListPresenter
@Inject constructor(private val getWallpapers: GetWallpapers,
                    private val loadWallpaper: LoadWallpaper,
                    private val previewWallpaper: PreviewWallpaper,
                    val advanceWallpaperItemMapper: AdvanceWallpaperItemMapper,
                    val wallpaperSwitcher: WallpaperSwitcher) : Presenter, DownloadListener {

    companion object {
        val TAG = "WallpaperListPresenter"
        val DOWNLOAD_STATE = "download_state"
        val DOWNLOADING_ITEM = "download_item"

        val DOWNLOAD_NONE = 0
        val DOWNLOADING = 1
        val DOWNLOAD_ERROR = 2

        private var currentPreviewing: WallpaperItem? = null
    }

    private val wallpaperObserver = WallpapersObserver()
    private var view: WallpaperListView? = null

    private var currentDownloadId: Long = -1
    private var downloadingWallpaper: WallpaperItem? = null

    private var downloadState = DOWNLOAD_NONE

    private val mContentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean, uri: Uri) {
            LogUtil.D(TAG, "Uri change." + uri)
            if (currentPreviewing != null) {
                view?.selectWallpaper(currentPreviewing!!)
            }
        }
    }

    fun setView(view: WallpaperListView) {
        this.view = view

        if (view.getWallpaperType() == WallpaperType.LIVE) {
            view.context().contentResolver.registerContentObserver(
                    AlbumContract.LiveWallpaper.CONTENT_SELECT_PREVIEWING_URI,
                    true, mContentObserver)
        } else if (view.getWallpaperType() == WallpaperType.STYLE) {
            view.context().contentResolver.registerContentObserver(
                    AlbumContract.StyleWallpaper.CONTENT_SELECT_PREVIEWING_URI,
                    true, mContentObserver)
        } else {
            view.context().contentResolver.registerContentObserver(
                    AlbumContract.VideoWallpaper.CONTENT_SELECT_PREVIEWING_URI,
                    true, mContentObserver)
        }

    }

    fun initialize(wallpaperType: WallpaperType) {
        view?.showLoading()
        getWallpapers.execute(wallpaperObserver,
                GetWallpapers.Params.withType(wallpaperType))
    }

    fun loadWallpapers(type: WallpaperType) {
        view?.showLoading()
        loadWallpaper.execute(object : DefaultObserver<List<Wallpaper>>() {
            override fun onNext(needDownload: List<Wallpaper>) {
                view?.renderWallpapers(advanceWallpaperItemMapper.transformList(needDownload))
            }

            override fun onComplete() {

            }

            override fun onError(exception: Throwable) {
                view?.showError(
                        ErrorMessageFactory.create(view!!.context(), exception as Exception))
                view?.showRetry()
            }
        }, LoadWallpaper.Params.withType(type))
    }

    fun previewWallpaper(item: WallpaperItem) {
        if (WallpaperFileHelper.isNeedDownloadWallpaper(item.lazyDownload,
                item.storePath) || (downloadingWallpaper != null
                && TextUtils.equals(downloadingWallpaper!!.wallpaperId, item.wallpaperId))) {
            view?.showDownloadHintDialog(item)
        } else {
            previewWallpaper.execute(object : DefaultObserver<Boolean>() {
                override fun onNext(success: Boolean) {
                    currentPreviewing = item
                    wallpaperSwitcher.switchService(view!!.context())
                }
            }, PreviewWallpaper.Params.previewWallpaper(item.wallpaperId, item.wallpaperType))
        }
    }

    fun requestDownload(item: WallpaperItem) {
        view?.showDownloadingDialog(item)
        downloadingWallpaper = item
        downloadState = DOWNLOADING
        val downloader = KinglloyDownloader.getInstance(view!!.context())
        val downloadRequest = KinglloyDownloader.Request(item.downloadUrl)
                .setDestinationPath(item.storePath)
        val downloadId = downloader.queryId(downloadRequest)
        if (downloader.getState(downloadId) == DownloadState.STATE_DOWNLOADING) {
            currentDownloadId = downloadId
        } else {
            if (downloadId != -1L) {
                currentDownloadId = downloadId
                downloader.start(currentDownloadId)
            } else {
                currentDownloadId = KinglloyDownloader.getInstance(view!!.context())
                        .enqueue(downloadRequest)
            }
        }

        KinglloyDownloader.getInstance(view!!.context()).registerListener(currentDownloadId, this)
    }

    fun cancelCurrentDownload() {
        KinglloyDownloader.getInstance(view!!.context()).cancel(currentDownloadId)
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(DOWNLOAD_STATE, downloadState)
        if (downloadingWallpaper != null) {
            outState.putParcelable(DOWNLOADING_ITEM, downloadingWallpaper!!)
        }
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        downloadState = savedInstanceState.getInt(DOWNLOAD_STATE)
        downloadingWallpaper = savedInstanceState.getParcelable(DOWNLOADING_ITEM)

        if (downloadingWallpaper != null) {
            if (downloadState == DOWNLOADING) {
                requestDownload(downloadingWallpaper!!)
            } else if (downloadState == DOWNLOAD_ERROR) {

            }
        }
    }

    fun getDownloadingItem(): WallpaperItem? = downloadingWallpaper

    override fun resume() {
    }

    override fun pause() {

    }

    override fun destroy() {
        KinglloyDownloader.getInstance(view!!.context())
                .unregisterListener(currentDownloadId, this)
        view!!.context().contentResolver.unregisterContentObserver(mContentObserver)
        getWallpapers.dispose()
        loadWallpaper.dispose()
        downloadingWallpaper = null
        view = null
    }

    override fun onDownloadPending(downloadId: Long) {

    }

    override fun onDownloadProgress(downloadId: Long, downloadedSize: Long, totalSize: Long) {
        view?.updateDownloadingProgress(downloadedSize)
    }

    override fun onDownloadPause(downloadId: Long) {

    }

    override fun onDownloadComplete(downloadId: Long, path: String?) {
        view?.downloadComplete(downloadingWallpaper!!)
        downloadingWallpaper = null
        currentDownloadId = -1
        downloadState = DOWNLOAD_NONE

        KinglloyDownloader.getInstance(view!!.context())
                .unregisterListener(downloadId, this)
    }

    override fun onDownloadError(downloadId: Long, errorCode: Int, errorMessage: String?) {
        if (errorCode == ErrorCode.ERROR_CONNECT_TIMEOUT) {
            view?.showDownloadError(downloadingWallpaper!!, NetworkConnectionException())
        } else {
            view?.showDownloadError(downloadingWallpaper!!, Exception(errorMessage))
        }
        downloadingWallpaper = null
        currentDownloadId = -1
        downloadState = DOWNLOAD_ERROR

        KinglloyDownloader.getInstance(view!!.context())
                .unregisterListener(downloadId, this)
    }

    private inner class WallpapersObserver : DefaultObserver<List<Wallpaper>>() {
        override fun onNext(needDownload: List<Wallpaper>) {
            if (needDownload.isEmpty()) {
                view?.showEmpty()
            } else {
                view?.renderWallpapers(advanceWallpaperItemMapper.transformList(needDownload))
            }
        }

        override fun onComplete() {
        }

        override fun onError(exception: Throwable?) {
        }
    }
}