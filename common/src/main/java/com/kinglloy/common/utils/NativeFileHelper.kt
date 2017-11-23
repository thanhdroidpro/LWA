package com.kinglloy.common.utils

import android.content.Context
import android.text.TextUtils
import java.io.File

/**
 * @author jinyalin
 * @since 2017/8/10.
 */

object NativeFileHelper {
    private val TAG = "NativeFileHelper"
    private var nativePath: String? = null

    fun getNativeDir(context: Context): File {
        if (TextUtils.isEmpty(nativePath)) {
            val cacheDir = context.cacheDir
            val nativeDir = File(cacheDir.parent, "files")
            nativeDir.mkdirs()
            nativePath = nativeDir.absolutePath
        }
        return File(nativePath)
    }

    fun getNativeFileName(componentPath: String, libName: String): String {
        return getNativeComponentPrefix(componentPath) + libName + ".so"
    }

    fun clearNativeFiles(context: Context, componentPath: String) {
        val files = getNativeFiles(context, componentPath)
        for (file in files) {
            file.delete()
        }
    }

    private fun getNativeComponentPrefix(componentPath: String): String {
        return "plugin_" + componentPath.hashCode() + "_"
    }

    private fun getNativeFiles(context: Context, componentPath: String): Array<File> {
        val nativePrefix = getNativeComponentPrefix(componentPath)
        return getNativeDir(context).listFiles({ file -> file.name.startsWith(nativePrefix) })
    }
}