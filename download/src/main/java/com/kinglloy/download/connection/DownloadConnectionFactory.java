package com.kinglloy.download.connection;

import com.kinglloy.download.connection.DownloadUrlConnection.Configuration;

import java.io.IOException;

/**
 * @author jinyalin
 * @since 2017/5/31.
 */

public class DownloadConnectionFactory {
    public static DownloadConnection create(String url) throws IOException {
        return new DownloadUrlConnection(url);
    }

    public static DownloadConnection create(String url, Configuration configuration)
            throws IOException {
        return new DownloadUrlConnection(url, configuration);
    }
}
