package com.kinglloy.album.view.component

import android.content.Context
import android.text.TextUtils
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.kinglloy.album.R
import com.kinglloy.album.model.WallpaperItem
import com.kinglloy.album.util.formatSizeToString

/**
 * @author jinyalin
 * *
 * @since 2017/8/11.
 */

class DownloadingDialog constructor(context: Context) {
    private var totalSizeString: String? = null

    private val dialog = MaterialDialog.Builder(context)
            .iconRes(R.drawable.advance_downloading)
            .title(R.string.downloading)
            .cancelable(false)
            .customView(R.layout.dialog_downloading, false).build()

    private val progressView = dialog.findViewById(R.id.downloadProgress) as TextView

    fun setTotalSize(totalSize: Long) {
        totalSizeString = if (totalSize > 0) formatSizeToString(totalSize) else null
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
    }

    fun showError(item: WallpaperItem, e: Exception) {

    }

}
