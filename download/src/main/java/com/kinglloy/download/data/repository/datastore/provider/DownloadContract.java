package com.kinglloy.download.data.repository.datastore.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author jinyalin
 * @since 2017/5/28.
 */

public class DownloadContract {
    public static final String AUTHORITY = "com.baidu.download";

    private static final String SCHEME = "content://";

    interface DownloadRequestColumns {
        /**
         * Type:INTEGER UNIQUE NOT NULL
         */
        String COLUMN_NAME_DOWNLOAD_ID = "download_id";

        /**
         * Type:TEXT
         */
        String COLUMN_NAME_DOWNLOAD_URI = "download_uri";

        /**
         * Type:TEXT
         */
        String COLUMN_NAME_DESTINATION_PATH = "destination_path";

        /**
         * Type:Text
         */
        String COLUMN_NAME_REQUEST_HEADERS = "request_headers";

        /**
         * Type:Text
         */
        String COLUMN_NAME_MIME_TYPE = "download_mime";

        /**
         * Type:INTEGER
         */
        String COLUMN_NAME_ALLOW_METERED = "allow_metered";

        /**
         * Type:INTEGER
         */
        String COLUMN_NAME_DOWNLOADED_SIZE = "downloaded_size";

        /**
         * Type:INTEGER
         */
        String COLUMN_NAME_TOTAL_SIZE = "total_size";

        /**
         * Type:INTEGER
         */
        String COLUMN_NAME_STATE = "download_state";

        /**
         * Type:TEXT
         */
        String COLUMN_NAME_ERROR_MSG = "error_msg";

        /**
         * Type:TEXT
         */
        String COLUMN_NAME_EXTRA = "extra";
    }

    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);
    private static final String PATH_DOWNLOAD_REQUEST = "download_request";
    private static final String PATH_QUERY_ID = "query_id";
    private static final String PATH_PROGRESS = "progress";
    private static final String PATH_STATE = "state";
    private static final String PATH_PAUSE_ALL = "pause_all";

    public static final class DownloadRequest implements DownloadRequestColumns, BaseColumns {
        static final String TABLE_NAME = "DownloadRequest";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DOWNLOAD_REQUEST).build();

        public static final Uri QUERY_ID_URI =
                CONTENT_URI.buildUpon().appendPath(PATH_QUERY_ID).build();

        public static final Uri PROGRESS_URI =
                CONTENT_URI.buildUpon().appendPath(PATH_PROGRESS).build();

        public static final Uri STATE_URI =
                CONTENT_URI.buildUpon().appendPath(PATH_STATE).build();

        public static final Uri PAUSE_ALL_URI =
                CONTENT_URI.buildUpon().appendPath(PATH_PAUSE_ALL).build();

        public static Uri buildDownloadRequestUri(long downloadId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(downloadId)).build();
        }

        public static long getDownloadRequestId(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}
