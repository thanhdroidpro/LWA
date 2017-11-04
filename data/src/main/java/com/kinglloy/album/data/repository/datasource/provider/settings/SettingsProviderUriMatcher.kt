package com.kinglloy.album.data.repository.datasource.provider.settings

import android.content.UriMatcher
import android.net.Uri
import android.util.SparseArray

/**
 * @author jinyalin
 * @since 2017/11/4.
 */
class SettingsProviderUriMatcher {

    private var mUriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    private val mEnumsMap = SparseArray<SettingsUriEnum>()

    init {
        buildUriMatcher()
    }

    private fun buildUriMatcher() {
        val authority = SettingsContract.AUTHORITY

        val uris = SettingsUriEnum.values()
        for (uri in uris) {
            mUriMatcher.addURI(authority, uri.path, uri.code)
        }
        buildEnumsMap()
    }

    private fun buildEnumsMap() {
        val uris = SettingsUriEnum.values()
        for (uri in uris) {
            mEnumsMap.put(uri.code, uri)
        }
    }

    fun matchUri(uri: Uri): SettingsUriEnum {
        val code = mUriMatcher.match(uri)
        try {
            return matchCode(code)
        } catch (e: UnsupportedOperationException) {
            throw UnsupportedOperationException("Unknown uri " + uri)
        }

    }

    fun matchCode(code: Int): SettingsUriEnum {
        val uriEnum = mEnumsMap.get(code)
        return uriEnum ?: throw UnsupportedOperationException("Unknown uri with code " + code)
    }
}