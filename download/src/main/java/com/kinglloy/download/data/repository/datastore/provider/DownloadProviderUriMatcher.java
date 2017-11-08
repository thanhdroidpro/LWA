package com.kinglloy.download.data.repository.datastore.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

/**
 * @author jinyalin
 * @since 2017/5/28.
 */

public class DownloadProviderUriMatcher {

    private UriMatcher mUriMatcher;

    private SparseArray<DownloadUriEnum> mEnumsMap = new SparseArray<>();

    public DownloadProviderUriMatcher() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        buildUriMatcher();
    }

    private void buildUriMatcher() {
        final String authority = DownloadContract.AUTHORITY;

        DownloadUriEnum[] uris = DownloadUriEnum.values();
        for (DownloadUriEnum uri : uris) {
            mUriMatcher.addURI(authority, uri.path, uri.code);
        }
        buildEnumsMap();
    }

    private void buildEnumsMap() {
        DownloadUriEnum[] uris = DownloadUriEnum.values();
        for (DownloadUriEnum uri : uris) {
            mEnumsMap.put(uri.code, uri);
        }
    }

    public DownloadUriEnum matchUri(Uri uri) {
        final int code = mUriMatcher.match(uri);
        try {
            return matchCode(code);
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Unknown uri " + uri);
        }
    }

    public DownloadUriEnum matchCode(int code) {
        DownloadUriEnum uriEnum = mEnumsMap.get(code);
        if (uriEnum != null) {
            return uriEnum;
        } else {
            throw new UnsupportedOperationException("Unknown uri with code " + code);
        }
    }
}
