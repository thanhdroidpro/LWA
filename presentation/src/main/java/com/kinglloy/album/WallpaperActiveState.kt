package com.kinglloy.album

import android.content.Context
import android.content.SharedPreferences


/**
 * @author jinyalin
 * @since 2017/9/26.
 */

private var preference: SharedPreferences? = null

private fun getPreference(context: Context): SharedPreferences {
    if (preference == null) {
        preference = context.getSharedPreferences("album_active_state", Context.MODE_PRIVATE)
    }
    return preference!!
}

private val ACTIVE_STATE_KEY = "active_state"
val ACTIVE_NONE = 0
val ACTIVE_ORIGINE = 1
val ACTIVE_MIRROR = 2

fun currentActiveState(context: Context): Int {
    return getPreference(context).getInt(ACTIVE_STATE_KEY, ACTIVE_NONE)
}

fun setActiveState(context: Context, newState: Int) {
    return getPreference(context).edit().putInt(ACTIVE_STATE_KEY, newState).apply()
}