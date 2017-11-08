package com.kinglloy.download.connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author jinyalin
 * @since 2017/5/31.
 */

public interface DownloadConnection {
    int NO_RESPONSE_CODE = 0;
    int RESPONSE_CODE_FROM_OFFSET = 1;

    void addHeader(String key, String value);

    boolean dispatchAddResumeOffset(String etag, long offset);

    InputStream getInputStream() throws IOException;

    Map<String, List<String>> getRequestHeaderFields();

    Map<String, List<String>> getResponseHeaderFields();

    String getResponseHeaderField(String key);

    void execute() throws IOException;

    int getResponseCode() throws IOException;

    void ending();
}
