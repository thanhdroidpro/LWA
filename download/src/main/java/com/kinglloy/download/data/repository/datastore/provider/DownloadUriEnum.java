package com.kinglloy.download.data.repository.datastore.provider;

import com.kinglloy.download.data.repository.datastore.provider.DownloadDatabase.Tables;

/**
 * @author jinyalin
 * @since 2017/5/28.
 */

enum DownloadUriEnum {
    DOWNLOAD_REQUEST(100, "download_request", Tables.DOWNLOAD_REQUEST),
    DOWNLOAD_REQUEST_QUERY_ID(102, "download_request/query_id", Tables.DOWNLOAD_REQUEST),
    DOWNLOAD_REQUEST_PROGRESS(103, "download_request/progress", Tables.DOWNLOAD_REQUEST),
    DOWNLOAD_REQUEST_STATE(104, "download_request/state", Tables.DOWNLOAD_REQUEST),
    DOWNLOAD_REQUEST_PAUSE_ALL(105, "download_request/pause_all", Tables.DOWNLOAD_REQUEST),
    DOWNLOAD_REQUEST_ID(101, "download_request/*", Tables.DOWNLOAD_REQUEST);

    public int code;
    public String path;
    public String table;

    DownloadUriEnum(int code, String path, String table) {
        this.code = code;
        this.path = path;
        this.table = table;
    }
}
