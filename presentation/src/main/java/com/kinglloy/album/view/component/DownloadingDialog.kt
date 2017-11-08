package com.kinglloy.album.view.component

import android.content.Context
import android.text.TextUtils
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.kinglloy.album.R
import com.kinglloy.album.model.WallpaperItem
import com.kinglloy.album.util.formatSizeToString
import me.zhanghai.android.materialprogressbar.MaterialProgressBar

/**
 * @author jinyalin
 * *
 * @since 2017/8/11.
 */

class DownloadingDialog constructor(context: Context, onCancel: MaterialDialog.SingleButtonCallback) {
    private var totalSizeString: String? = null
    private var totalSize = 0L

    private val dialog = MaterialDialog.Builder(context)
            .iconRes(R.drawable.advance_downloading)
            .title(R.string.downloading)
            .cancelable(false)
            .positiveText(R.string.string_cancel)
            .onPositive(onCancel)
            .customView(R.layout.dialog_downloading, false).build()

    private val progressView = dialog.findViewById(R.id.downloadProgress) as TextView
    private val progressBar = dialog.findViewById(R.id.downloadProgressBar) as MaterialProgressBar

    fun setTotalSize(totalSize: Long) {
        this.totalSize = totalSize
        totalSizeString = if (totalSize > 0) formatSizeToString(totalSize) else null

        updateProgress(0)
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    fun updateProgress(progress: Long) {
        val showString =
                if (TextUtils.isEmpty(totalSizeString)) formatSizeToString(progress)
                else "${formatSizeToString(progress)} / $totalSizeString"
        progressView.text = showString

        val percent = ((progress / totalSize.toFloat()) * 100).toInt()
        progressBar.progress = percent
    }

    fun showError(item: WallpaperItem, e: Exception) {

    }

}
