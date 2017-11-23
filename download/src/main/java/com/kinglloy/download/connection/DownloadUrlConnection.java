package com.kinglloy.download.connection;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * @author jinyalin
 * @since 2017/5/31.
 */

class DownloadUrlConnection implements DownloadConnection {
    protected URLConnection urlConnection;

    public DownloadUrlConnection(String originUrl,
                                 Configuration configuration) throws IOException {
        this(new URL(originUrl), configuration);
    }

    public DownloadUrlConnection(URL url, Configuration configuration) throws IOException {
        if (configuration != null && configuration.proxy != null) {
            urlConnection = url.openConnection(configuration.proxy);
        } else {
            urlConnection = url.openConnection();
        }

        if (configuration != null) {
            if (configuration.readTimeout != null) {
                urlConnection.setReadTimeout(configuration.readTimeout);
            }

            if (configuration.connectTimeout != null) {
                urlConnection.setConnectTimeout(configuration.connectTimeout);
            }
        }
    }

    public DownloadUrlConnection(String originUrl) throws IOException {
        this(originUrl, null);
    }

    @Override
    public void addHeader(String key, String value) {
        urlConnection.addRequestProperty(key, value);
    }

    @Override
    public boolean dispatchAddResumeOffset(String etag, long offset) {
        return false;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return urlConnection.getInputStream();
    }

    @Override
    public Map<String, List<String>> getRequestHeaderFields() {
        return urlConnection.getRequestProperties();
    }

    @Override
    public Map<String, List<String>> getResponseHeaderFields() {
        return urlConnection.getHeaderFields();
    }

    @Override
    public String getResponseHeaderField(String key) {
        return urlConnection.getHeaderField(key);
    }

    @Override
    public void execute() throws IOException {
        urlConnection.connect();
    }

    @Override
    public int getResponseCode() throws IOException {
        if (urlConnection instanceof HttpURLConnection) {
            return ((HttpURLConnection) urlConnection).getResponseCode();
        }

        return DownloadConnection.NO_RESPONSE_CODE;
    }

    @Override
    public void ending() {
        // for reuse,so do nothing.
    }

    public static class Configuration {
        private Proxy proxy;
        private Integer readTimeout;
        private Integer connectTimeout;

        /**
         * The connection will be made through the specified proxy.
         * <p>
         * This {@code proxy} will be used when invoke {@link URL#openConnection(Proxy)}
         *
         * @param proxy the proxy will be applied to the {@link DownloadUrlConnection}
         */
        public Configuration proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        /**
         * Sets the read timeout to a specified timeout, in milliseconds. A non-zero value specifies
         * the timeout when reading from Input stream when a connection is established to a resource.
         * If the timeout expires before there is data available for read, a
         * java.net.SocketTimeoutException is raised. A timeout of zero is interpreted as an
         * infinite timeout.
         * <p>
         * This {@code readTimeout} will be applied through {@link URLConnection#setReadTimeout(int)}
         *
         * @param readTimeout an <code>int</code> that specifies the timeout value to be used in
         *                    milliseconds
         */
        public Configuration readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * Sets a specified timeout value, in milliseconds, to be used when opening a communications
         * link to the resource referenced by this URLConnection.  If the timeout expires before the
         * connection can be established, a java.net.SocketTimeoutException is raised. A timeout of
         * zero is interpreted as an infinite timeout.
         * <p>
         * This {@code connectionTimeout} will be applied through {@link URLConnection#setConnectTimeout(int)}
         *
         * @param connectTimeout an <code>int</code> that specifies the connect timeout value in
         *                       milliseconds
         */
        public Configuration connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }
    }
}
