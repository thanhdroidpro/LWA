package com.kinglloy.album.data.repository.datasource

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author jinyalin
 * @since 2017/11/4.
 */
@Singleton
class SettingsDataStoreFactory @Inject constructor(val context: Context) {
    fun createSettingsDataStore(): SettingsDataStore = SettingsDataStoreImpl(context)
}