package com.kinglloy.album.util

/**
 * @author jinyalin
 * @since 2017/11/3.
 */
private val C = 1024

fun formatSizeToString(size: Long): String {
    if (size < C) {
        return "$size B"
    }
    if (size < C * C) {
        return "${size / C} KB"
    }
    return "%.2f MB".format(size / (C * C).toFloat())
}