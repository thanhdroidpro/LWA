package com.kinglloy.album.data.repository.datasource.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

/**
 * YaLin 2016/12/30.
 */

public class AlbumProviderUriMatcher {

    private UriMatcher mUriMatcher;

    private SparseArray<AlbumUriEnum> mEnumsMap = new SparseArray<>();

    public AlbumProviderUriMatcher() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        buildUriMatcher();
    }

    private void buildUriMatcher() {
        final String authority = AlbumContract.AUTHORITY;

        AlbumUriEnum[] uris = AlbumUriEnum.values();
        for (AlbumUriEnum uri : uris) {
            mUriMatcher.addURI(authority, uri.path, uri.code);
        }
        buildEnumsMap();
    }

    private void buildEnumsMap() {
        AlbumUriEnum[] uris = AlbumUriEnum.values();
        for (AlbumUriEnum uri : uris) {
            mEnumsMap.put(uri.code, uri);
        }
    }

    public AlbumUriEnum matchUri(Uri uri) {
        final int code = mUriMatcher.match(uri);
        try {
            return matchCode(code);
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Unknown uri " + uri);
        }
    }

    public AlbumUriEnum matchCode(int code) {
        AlbumUriEnum uriEnum = mEnumsMap.get(code);
        if (uriEnum != null) {
            return uriEnum;
        } else {
            throw new UnsupportedOperationException("Unknown uri with code " + code);
        }
    }
}
