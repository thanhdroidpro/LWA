package com.kinglloy.album.view

import com.kinglloy.album.model.StyleSettingsItem

/**
 * @author jinyalin
 * @since 2017/11/4.
 */
interface SettingsView {
    fun renderStyleSettings(styleSettingsItem: StyleSettingsItem)
}