package com.kinglloy.album.util

import android.content.Context
import android.text.TextUtils

/**
 * @author jinyalin
 * @since 2017/11/27.
 */
object PackageUtil {
    fun isUltimate(context: Context): Boolean {
        return TextUtils.equals(context.packageName, "com.kinglloy.album.ultimate")
    }
}