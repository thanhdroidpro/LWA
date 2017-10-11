package com.kinglloy.album.data.repository.datasource.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author jinyalin
 * @since 2017/9/26.
 */

public class AlbumContract {
    public static final String AUTHORITY = "com.kinglloy.album";

    private static final String SCHEME = "content://";

    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

    private static final String PATH_ADVANCE_WALLPAPER = "advance_wallpaper";
    private static final String PATH_ACTIVE_SERVICE = "active_service";
    public static final String[] TOP_LEVEL_PATHS = {
            PATH_ADVANCE_WALLPAPER
    };

    interface AdvanceWallpaperColumns {
        /**
         * Type: TEXT
         */
        String COLUMN_NAME_WALLPAPER_ID = "wallpaper_id";
        /**
         * Type: TEXT
         */
        String COLUMN_NAME_ICON_URL = "icon_url";
        /**
         * Type: TEXT
         */
        String COLUMN_NAME_NAME = "name";
        /**
         * Type: TEXT
         */
        String COLUMN_NAME_LINK = "link";
        /**
         * Type: TEXT
         */
        String COLUMN_NAME_AUTHOR = "author";
        /**
         * Type: TEXT
         */
        String COLUMN_NAME_DOWNLOAD_URL = "download_url";
        /**
         * Type: TEXT
         */
        String COLUMN_NAME_CHECKSUM = "checksum";
        /**
         * Type: TEXT
         */
        String COLUMN_NAME_STORE_PATH = "store_path";
        /**
         * Type: TEXT
         */
        String COLUMN_NAME_PROVIDER_NAME = "provider_name";
        /**
         * Type: INTEGER
         */
        String COLUMN_NAME_SELECTED = "selected";

        /**
         * Type: INTEGER
         */
        String COLUMN_NAME_LAZY_DOWNLOAD = "lazy_download";

        /**
         * Type: INTEGER
         */
        String COLUMN_NAME_PREVIEWING = "previewing";
    }

    interface ActiveServiceColumns {
        /**
         * Type: INTEGER range: 0 1 2
         */
        String COLUMN_NAME_SERVICE_ID = "service_id";
    }

    public static final class AdvanceWallpaper implements AdvanceWallpaperColumns, BaseColumns {
        public static final String TABLE_NAME = "advance_wallpaper";

        public static final String PATH_SELECTED_WALLPAPER = "selected";
        public static final String PATH_PREVIEWING_WALLPAPER = "previewing";
        public static final String PATH_SELECT_PREVIEWING_WALLPAPER = "select_previewing";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_ADVANCE_WALLPAPER).build();

        public static final Uri CONTENT_SELECTED_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ADVANCE_WALLPAPER)
                        .appendPath(PATH_SELECTED_WALLPAPER).build();

        public static final Uri CONTENT_PREVIEWING_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ADVANCE_WALLPAPER)
                        .appendPath(PATH_PREVIEWING_WALLPAPER).build();

        public static final Uri CONTENT_SELECT_PREVIEWING_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ADVANCE_WALLPAPER)
                        .appendPath(PATH_SELECT_PREVIEWING_WALLPAPER).build();


        public static Uri buildWallpaperUri(String wallpaperId) {
            return CONTENT_URI.buildUpon().appendPath(wallpaperId).build();
        }

        public static String getWallpaperId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class ActiveService implements ActiveServiceColumns, BaseColumns {
        public static final String TABLE_NAME = "active_service";
        public static final int SERVICE_NONE = 0;
        public static final int SERVICE_ORIGIN = 1;
        public static final int SERVICE_MIRROR = 2;

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_ACTIVE_SERVICE).build();

    }
}