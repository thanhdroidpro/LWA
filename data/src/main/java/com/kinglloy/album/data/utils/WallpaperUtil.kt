package com.kinglloy.album.data.utils

import android.content.Context
import android.net.Uri
import com.kinglloy.album.data.repository.datasource.provider.AlbumContractHelper

/**
 * YaLin
 * On 2017/5/26.
 */
fun notifyChange(context: Context, uri: Uri) {
    if (!AlbumContractHelper.isUriCalledFromSyncAdapter(uri)) {
        context.contentResolver.notifyChange(uri, null)
    }
}