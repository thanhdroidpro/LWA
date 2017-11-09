package com.kinglloy.album.presenter

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import com.kinglloy.album.WallpaperSwitcher
import com.kinglloy.album.data.log.LogUtil
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import com.kinglloy.album.domain.Wallpaper
import com.kinglloy.album.domain.interactor.DefaultObserver
import com.kinglloy.album.domain.interactor.DeleteDownloadedWallpapers
import com.kinglloy.album.domain.interactor.GetDownloadedWallpapers
import com.kinglloy.album.domain.interactor.PreviewWallpaper
import com.kinglloy.album.mapper.WallpaperItemMapper
import com.kinglloy.album.model.WallpaperItem
import com.kinglloy.album.view.MyWallpapersView
import javax.inject.Inject

/**
 * @author jinyalin
 * @since 2017/11/9.
 */
class MyWallpapersPresenter
@Inject constructor(private val getDownloadedWallpapers: GetDownloadedWallpapers,
                    private val previewWallpaper: PreviewWallpaper,
                    val wallpaperSwitcher: WallpaperSwitcher,
                    val wallpaperItemMapper: WallpaperItemMapper,
                    private val deleteDownloadedWallpapers: DeleteDownloadedWallpapers) : Presenter {

    companion object {
        val TAG = "MyWallpapersPresenter"

        val UNDO_SHOW_TIME = 5000L

        private var currentPreviewing: WallpaperItem? = null
    }

    private lateinit var view: MyWallpapersView

    private var allWallpapers = ArrayList<WallpaperItem>()

    private var currentDeleting: ArrayList<WallpaperItem>? = null

    private val deleteHandler = Handler()
    private val deleteRunnable = Runnable {
        view.closeUndoDelete()
        if (currentDeleting != null && currentDeleting!!.size > 0) {
            val filesPath = ArrayList<String>()
            currentDeleting!!.mapTo(filesPath) { it.storePath }
            deleteDownloadedWallpapers.execute(object : DefaultObserver<Boolean>() {
                override fun onNext(success: Boolean) {
                    if (success) {
                        allWallpapers.removeAll(currentDeleting!!)
                        currentDeleting!!.clear()
                    }
                }
            }, DeleteDownloadedWallpapers.Params.withPaths(filesPath))
        }
    }

    private val mContentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean, uri: Uri) {
            LogUtil.D(TAG, "Uri change." + uri)
            if (currentPreviewing != null) {
                view.selectWallpaper(currentPreviewing!!)
            }
        }
    }

    fun setView(view: MyWallpapersView) {
        this.view = view

        view.context().contentResolver.registerContentObserver(
                AlbumContract.VideoWallpaper.CONTENT_SELECT_PREVIEWING_URI,
                true, mContentObserver)
        view.context().contentResolver.registerContentObserver(
                AlbumContract.LiveWallpaper.CONTENT_SELECT_PREVIEWING_URI,
                true, mContentObserver)
        view.context().contentResolver.registerContentObserver(
                AlbumContract.StyleWallpaper.CONTENT_SELECT_PREVIEWING_URI,
                true, mContentObserver)
    }

    fun initialize() {
        getDownloadedWallpapers.execute(object : DefaultObserver<List<Wallpaper>>() {
            override fun onNext(wallpapers: List<Wallpaper>) {
                allWallpapers.clear()
                if (wallpapers.isEmpty()) {
                    view.showEmpty()
                } else {
                    allWallpapers.addAll(wallpaperItemMapper.transformList(wallpapers))
                    view.renderWallpapers(allWallpapers)
                }
            }
        }, null)
    }

    fun undoDelete() {
        deleteHandler.removeCallbacks(deleteRunnable)
        view.renderWallpapers(allWallpapers)
    }

    override fun resume() {

    }

    override fun pause() {

    }

    override fun destroy() {
        view.context().contentResolver.unregisterContentObserver(mContentObserver)
        getDownloadedWallpapers.dispose()
        previewWallpaper.dispose()
        currentPreviewing = null
    }

    fun previewWallpaper(item: WallpaperItem) {
        previewWallpaper.execute(object : DefaultObserver<Boolean>() {
            override fun onNext(success: Boolean) {
                currentPreviewing = item
                wallpaperSwitcher.switchService(view!!.context())
            }
        }, PreviewWallpaper.Params.previewWallpaper(item.wallpaperId, item.wallpaperType))
    }

    fun deleteDownloadedWallpapers(wallpapers: ArrayList<WallpaperItem>) {
        view.showUndoDelete()
        currentDeleting = wallpapers
        deleteHandler.postDelayed(deleteRunnable, UNDO_SHOW_TIME)
    }

}