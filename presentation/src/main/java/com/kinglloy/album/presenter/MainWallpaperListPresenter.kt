package com.kinglloy.album.presenter

import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import com.kinglloy.album.WallpaperSwitcher
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
import javax.inject.Inject

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
class MainWallpaperListPresenter
@Inject constructor(private val getWallpapers: GetWallpapers,
                    private val loadWallpaper: LoadWallpaper,
                    private val previewWallpaper: PreviewWallpaper,
                    val advanceWallpaperItemMapper: AdvanceWallpaperItemMapper,
                    val wallpaperSwitcher: WallpaperSwitcher)
    : Presenter {

    companion object {
        val TAG = "WallpaperListPresenter"
        val DOWNLOAD_STATE = "download_state"
        val DOWNLOADING_ITEM = "download_item"

        val DOWNLOAD_NONE = 0
        val DOWNLOADING = 1
        val DOWNLOAD_ERROR = 2
    }

    private val wallpaperObserver = WallpapersObserver()
    private var view: WallpaperListView? = null

    private var downloadingWallpaper: WallpaperItem? = null

    private var downloadState = DOWNLOAD_NONE

    private var currentPreviewing: WallpaperItem? = null

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

        view.context().contentResolver.registerContentObserver(
                AlbumContract.LiveWallpaper.CONTENT_SELECT_PREVIEWING_URI,
                true, mContentObserver)
    }

    fun initialize() {
        view?.showLoading()
        getWallpapers.execute(wallpaperObserver, null)
    }

    fun loadAdvanceWallpaper() {
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
        }, null)
    }

    fun previewWallpaper(item: WallpaperItem, type: WallpaperType) {
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
            }, PreviewWallpaper.Params.previewWallpaper(item.wallpaperId, type))
        }
    }

    fun requestDownload(item: WallpaperItem) {
        view?.showDownloadingDialog(item)
        downloadingWallpaper = item
        downloadState = DOWNLOADING

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
        view!!.context().contentResolver.unregisterContentObserver(mContentObserver)
        getWallpapers.dispose()
        loadWallpaper.dispose()
        downloadingWallpaper = null
        view = null
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