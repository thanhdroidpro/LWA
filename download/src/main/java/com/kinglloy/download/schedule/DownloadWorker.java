package com.kinglloy.download.schedule;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Pair;


import com.kinglloy.download.KinglloyDownloader;
import com.kinglloy.download.KinglloyDownloader.Request;
import com.kinglloy.download.connection.DownloadConnection;
import com.kinglloy.download.connection.DownloadConnectionFactory;
import com.kinglloy.download.domain.iteractor.UpdateProgress;
import com.kinglloy.download.domain.iteractor.UpdateState;
import com.kinglloy.download.exceptions.DownloadHttpException;
import com.kinglloy.download.exceptions.DownloadOutOfSpaceException;
import com.kinglloy.download.exceptions.ErrorCode;
import com.kinglloy.download.exceptions.RequestDuplicateException;
import com.kinglloy.download.exceptions.RequestNotExistException;
import com.kinglloy.download.module.InternalRequest;
import com.kinglloy.download.observable.DownloadObservableImpl;
import com.kinglloy.download.state.DownloadState;
import com.kinglloy.download.stream.DownloadOutput;
import com.kinglloy.download.stream.DownloadOutputFactory;
import com.kinglloy.download.utils.DownloadExecutors;
import com.kinglloy.download.utils.DownloadFileUtil;
import com.kinglloy.download.utils.DownloadUtil;
import com.kinglloy.download.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jinyalin
 * @since 2017/5/26.
 */

class DownloadWorker {
    private static final String TAG = "Worker";

    private static final class InstanceHolder {
        private static DownloadWorker INSTANCE = new DownloadWorker();
    }

    private static final int HTTP_REQUESTED_RANGE_NOT_SATISFIABLE = 416;

    private static final int TOTAL_VALUE_IN_CHUNKED_RESOURCE = -1;
    private static final int MAX_RETRY_TIMES = 3;

    private static final int BUFFER_SIZE = 1024 * 4;

    private static final int CALL_BACK_MIN_INTERVAL_BYTES = 1024 * 2;
    private static final long CALL_BACK_MIN_INTERVAL_TIMES = 300;


    static DownloadWorker getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private ThreadPoolExecutor mPool;

    private LinkedBlockingQueue<Runnable> mWorkQueue;

    private static Map<Long, LaunchRequestRunnable> mPendingRequests = new ConcurrentHashMap<>();

    private static UpdateProgress mUpdateProgressUseCase;
    private static UpdateState mUpdateStateUseCase;

    private DownloadWorker() {
        init();
        mUpdateProgressUseCase = new UpdateProgress();
        mUpdateStateUseCase = new UpdateState();
    }

    private void init() {
        mWorkQueue = new LinkedBlockingQueue<>();
        mPool = DownloadExecutors.newDefaultThreadPool(3, mWorkQueue, "LauncherTask");
    }

    void execute(IdRequest request) throws RequestDuplicateException {
        if (!mPendingRequests.containsKey(request.getId())) {
            LogUtil.D(TAG, "execute request " + request.getId());

            request.getOrigin().setDownloadState(DownloadState.STATE_PENDING);
            mUpdateStateUseCase.execute(UpdateState.Params.updateState(request));

            LaunchRequestRunnable r = new LaunchRequestRunnable(request);
            mPendingRequests.put(request.getId(), r);
            mPool.execute(r);

            DownloadObservableImpl.getInstance()
                    .notifyDownloadStateChange(request, DownloadState.STATE_PENDING);
        } else {
            throw new RequestDuplicateException(request.getId(), "Request : "
                    + request.getId() + " is already exist.");
        }
    }

    void start(IdRequest idRequest) throws RequestNotExistException, RequestDuplicateException {
        if (idRequest.getOrigin().getDownloadState() == DownloadState.STATE_DOWNLOADING) {
            throw new RequestDuplicateException(idRequest.getId(),
                    "Request " + idRequest.getId() + " has already started.");
        }
        execute(idRequest);
    }

    void pause(long id) {
        if (mPendingRequests.containsKey(id)) {
            LaunchRequestRunnable r = mPendingRequests.get(id);
            r.pause();
            mPendingRequests.remove(id);
        }
    }

    void cancel(long id) {
        if (mPendingRequests.containsKey(id)) {
            LaunchRequestRunnable r = mPendingRequests.get(id);
            r.cancel();
            mPendingRequests.remove(id);
        }
    }

    public void expireAll() {
        LogUtil.D(TAG, "expire " + mWorkQueue.size() + " tasks");

        mPool.shutdownNow();
        init();
    }

    private static class LaunchRequestRunnable implements Runnable {
        private IdRequest mRequest;

        private boolean mPaused = false;
        private boolean mCanceled = false;

        boolean revisedInterval = false;

        LaunchRequestRunnable(IdRequest request) {
            mRequest = request;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            Request request = mRequest.getOrigin();
            if (!request.isMeteredAllowed()) {
                if (Build.VERSION.SDK_INT >= 23 && KinglloyDownloader.getContext()
                        .checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    LogUtil.D(TAG, String.format(Locale.getDefault(),
                            "Task[%d] can't start the download runnable," +
                                    " because this task require wifi, but user application " +
                                    "nor current process has %s, so we can't check whether " +
                                    "the network type connection.", mRequest.getId(),
                            Manifest.permission.ACCESS_NETWORK_STATE));
                    DownloadObservableImpl.getInstance().notifyError(mRequest.getId(),
                            0, "Permission for ACCESS_NETWORK_STATE not granted");

                    return;
                } else {
                    ConnectivityManager connectivityManager =
                            (ConnectivityManager) KinglloyDownloader.getContext()
                                    .getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (Build.VERSION.SDK_INT >= 16
                            && connectivityManager.isActiveNetworkMetered()) {
                        DownloadObservableImpl.getInstance().notifyError(mRequest.getId(),
                                0, "Wifi network not available.");
                        return;
                    }
                }
            }

            LogUtil.D(TAG, "Downloading request: " + mRequest.getOrigin().getUri());

            loop();
        }

        private void loop() {
            int retryingTimes = 0;
            DownloadConnection connection = null;
            InternalRequest request = mRequest.getOrigin();
            long soFar;
            final long id = mRequest.getId();

            do {
                try {
                    if (checkPause()) {
                        onPause();
                        break;
                    }

                    String tempPath = DownloadFileUtil.getTempPath(request.getDestinationPath());
                    File outputFile = new File(tempPath);
                    // error
                    if (request.getDownloadedSize() > outputFile.length()) {
                        request.setDownloadedSize(0);
                        //noinspection ResultOfMethodCallIgnored
                        outputFile.delete();
                    }
                    soFar = request.getDownloadedSize();

                    updateState(DownloadState.STATE_DOWNLOADING);
                    connection = DownloadConnectionFactory.create(request.getUri().toString());
                    addHeader(connection, soFar);

                    final Map<String, List<String>> requestHeader
                            = connection.getRequestHeaderFields();
                    LogUtil.D(TAG, id + " request header " + requestHeader);

                    connection.execute();

                    final int code = connection.getResponseCode();

                    final boolean isSucceedStart =
                            code == HttpURLConnection.HTTP_OK
                                    || code == DownloadConnection.NO_RESPONSE_CODE;
                    // if the response status code isn't point to PARTIAL/OFFSET, isSucceedResume will
                    // be assigned to false, so filedownloader will download the file from very beginning.
                    final boolean isSucceedResume =
                            ((code == HttpURLConnection.HTTP_PARTIAL)
                                    || (code == DownloadConnection.RESPONSE_CODE_FROM_OFFSET));

                    if (soFar > 0 && !isSucceedResume) {
                        LogUtil.E(TAG, "want to start from the breakpoint" + soFar + ", but the " +
                                "response status code is " + code);
                    }

                    if (isSucceedStart || isSucceedResume) {
                        long total = request.getTotalSize();
                        final String transferEncoding =
                                connection.getResponseHeaderField("Transfer-Encoding");

                        if (isSucceedStart || total <= 0) {
                            if (transferEncoding == null) {
                                total = DownloadUtil.convertContentLengthString(
                                        connection.getResponseHeaderField("Content-Length"));
                            } else {
                                // if transfer not nil, ignore content-length
                                total = TOTAL_VALUE_IN_CHUNKED_RESOURCE;
                            }
                        }

                        if (total < 0) {
                            // invalid total length
                            final boolean isEncodingChunked = transferEncoding != null
                                    && transferEncoding.equals("chunked");
                            if (!isEncodingChunked) {
                                throw new IOException("can't know the size of the " +
                                        "download file, and its Transfer-Encoding is not Chunked " +
                                        "either.\nyou can ignore such exception by add " +
                                        "http.lenient=true to the filedownloader.properties");

                            }
                        }

                        if (!isSucceedResume) {
                            soFar = 0;
                        }

                        request.setDownloadedSize(soFar);
                        request.setTotalSize(total);

                        if (fetch(connection, isSucceedResume, soFar, total)) {
                            if (checkCancel()) {
                                //noinspection ResultOfMethodCallIgnored
                                outputFile.delete();
                                updateState(DownloadState.STATE_CANCEL,
                                        "Download canceled.");
                            }
                            break;
                        }
                    } else {
                        final DownloadHttpException httpException =
                                new DownloadHttpException(code,
                                        requestHeader, connection.getResponseHeaderFields());
                        if (revisedInterval) {
                            throw httpException;
                        }
                        revisedInterval = true;
                        switch (code) {
                            case HTTP_REQUESTED_RANGE_NOT_SATISFIABLE:
                                deleteTaskFiles();
                                onRetry(httpException, retryingTimes++);
                                break;
                            default:
                                throw httpException;
                        }
                    }

                } catch (Throwable e) {
                    LogUtil.E(TAG, e);
                    if (++retryingTimes > MAX_RETRY_TIMES) {
                        updateState(DownloadState.STATE_ERROR,
                                "Retry time max error.");
                        DownloadObservableImpl.getInstance()
                                .notifyError(mRequest, ErrorCode.ERROR_CONNECT_TIMEOUT,
                                        "Retry time max error.");
                        break;
                    } else {
                        onRetry(e, retryingTimes);
                    }
                } finally {
                    if (connection != null) {
                        connection.ending();
                    }
                }

            } while (true);
        }

        private void onRetry(Throwable ex, final int retryTimes) {
            LogUtil.D(TAG, String.format(Locale.ENGLISH,
                    "On retry id = %d exception: %s retry time = %d", mRequest.getId(), ex,
                    retryTimes));

        }

        private boolean fetch(DownloadConnection connection, boolean isSucceedContinue,
                              long soFar, long total) throws Throwable {
            // fetching datum
            InputStream inputStream = null;
            final DownloadOutput output = getOutput(isSucceedContinue, soFar, total);

            try {
                inputStream = connection.getInputStream();
                byte[] buff = new byte[BUFFER_SIZE];

                do {
                    int byteCount = inputStream.read(buff);
                    if (byteCount == -1) {
                        break;
                    }
                    output.write(buff, 0, byteCount);

                    soFar += byteCount;

                    onProgress(soFar, total, output);

                    if (checkPause()) {
                        mUpdateProgressUseCase
                                .execute(UpdateProgress.Params.updateProgress(mRequest));
                        output.sync();
                        onPause();
                        return true;
                    }
                } while (true);

                if (total == TOTAL_VALUE_IN_CHUNKED_RESOURCE) {
                    total = soFar;
                }
                if (soFar == total) {

                    onComplete(total);
                    renameTempFile();

                    return true;
                } else {
                    throw new RuntimeException(
                            String.format(Locale.ENGLISH, "sofar[%d] not equal total[%d]",
                                    soFar, total));
                }
            } finally {
                if (inputStream != null) {
                    //noinspection ThrowFromFinallyBlock
                    inputStream.close();
                }

                try {
                    if (output != null) {
                        //noinspection ThrowFromFinallyBlock
                        output.sync();
                    }
                } finally {
                    //noinspection ConstantConditions
                    if (output != null) {
                        //noinspection ThrowFromFinallyBlock
                        output.close();
                    }
                }
            }
        }

        private DownloadOutput getOutput(boolean append, long soFar, long total)
                throws IOException, IllegalAccessException {
            final String tempPath = DownloadFileUtil.getTempPath(
                    mRequest.getOrigin().getDestinationPath());
            if (TextUtils.isEmpty(tempPath)) {
                throw new RuntimeException("found invalid internal destination path, empty");
            }

            File file = new File(tempPath);

            if (file.exists() && file.isDirectory()) {
                throw new RuntimeException(
                        String.format("found invalid internal destination path[%s]," +
                                " & path is directory[%B]", tempPath, file.isDirectory()));
            }
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new IOException(
                            String.format("createDbStore new file error  %s",
                                    file.getAbsolutePath()));
                }
            }

            DownloadOutput output = DownloadOutputFactory.create(file);

            // check the available space bytes whether enough or not.
            if (total > 0) {
                final long breakpointBytes = file.length();
                final long requiredSpaceBytes = total - breakpointBytes;

                final long freeSpaceBytes = DownloadFileUtil.getFreeSpaceBytes(tempPath);

                if (freeSpaceBytes < requiredSpaceBytes) {
                    output.close();
                    // throw a out of space exception.
                    throw new DownloadOutOfSpaceException(freeSpaceBytes,
                            requiredSpaceBytes, breakpointBytes);
                } else {
                    // pre allocate.
                    output.setLength(total);
                }
            }

            if (append) {
                output.seek(soFar);
            }

            return output;
        }

        private long lastCallbackBytes = 0;
        private long lastCallbackTime = 0;

        private long lastUpdateBytes = 0;
        private long lastUpdateTime = 0;

        private void onProgress(final long soFar, final long total,
                                final DownloadOutput output) {
//            LogUtil.D(TAG, "On progress " + mRequest.getId() + " sofar " + soFar + " total " + total);
            mRequest.getOrigin().setDownloadedSize(soFar);

            if (soFar == total) {
                DownloadObservableImpl.getInstance()
                        .notifyDownloadProgress(mRequest, soFar, total);
                return;
            }

            final long now = SystemClock.elapsedRealtime();
            final long bytesDelta = soFar - lastUpdateBytes;
            final long timeDelta = now - lastUpdateTime;

            if (bytesDelta > DownloadFileUtil.getMinProgressStep() &&
                    timeDelta > DownloadFileUtil.getMinProgressTime()) {
                try {
                    mUpdateProgressUseCase.execute(UpdateProgress.Params.updateProgress(mRequest));
                    output.sync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                lastUpdateBytes = soFar;
                lastUpdateTime = now;
            }

            final long callbackBytesDelta = soFar - lastCallbackBytes;
            final long callbackTimeDelta = now - lastCallbackTime;

            if (callbackBytesDelta < CALL_BACK_MIN_INTERVAL_BYTES) {
                return;
            }

            if (callbackTimeDelta < CALL_BACK_MIN_INTERVAL_TIMES) {
                return;
            }

            lastCallbackTime = now;
            lastCallbackBytes = soFar;

            DownloadObservableImpl.getInstance()
                    .notifyDownloadProgress(mRequest, soFar, total);
        }

        private void onPause() {
            updateState(DownloadState.STATE_PAUSE);
            DownloadObservableImpl.getInstance()
                    .notifyDownloadStateChange(mRequest, DownloadState.STATE_PAUSE);
            LogUtil.D(TAG, "Request paused " + mRequest.getId());
        }

        private void onComplete(long totalSize) {
            mRequest.getOrigin().setDownloadedSize(totalSize);
            mRequest.getOrigin().setTotalSize(totalSize);

            mUpdateProgressUseCase.execute(UpdateProgress.Params.updateProgress(mRequest));

            mPendingRequests.remove(mRequest.getId());

            updateState(DownloadState.STATE_COMPLETE);

            DownloadObservableImpl.getInstance()
                    .notifyDownloadStateChange(mRequest, DownloadState.STATE_COMPLETE);
        }

        private void addHeader(DownloadConnection connection, long soFar) {
            for (Pair<String, String> header : mRequest.getOrigin().getRequestHeaders()) {
                connection.addHeader(header.first, header.second);
            }

            if (!connection.dispatchAddResumeOffset(null, soFar)) {
//                if (!TextUtils.isEmpty(etag)) {
//                    connection.addHeader("If-Match", etag);
//                }
                connection.addHeader("Range", String.format(Locale.ENGLISH, "bytes=%d-", soFar));
            }
        }

        private void renameTempFile() {
            final String targetPath = mRequest.getOrigin().getDestinationPath();
            final String tempPath = DownloadFileUtil.getTempPath(targetPath);

            final File tempFile = new File(tempPath);
            try {
                final File targetFile = new File(targetPath);

                if (targetFile.exists()) {
                    final long oldTargetFileLength = targetFile.length();
                    if (!targetFile.delete()) {
                        throw new IllegalStateException(String.format(Locale.ENGLISH,
                                "Can't delete the old file([%s], [%d]), " +
                                        "so can't replace it with the new downloaded one.",
                                targetPath, oldTargetFileLength
                        ));
                    } else {
                        LogUtil.D(TAG, String.format(Locale.ENGLISH, "The target file([%s], [%d]) will be replaced with" +
                                        " the new downloaded file[%d]",
                                targetPath, oldTargetFileLength, tempFile.length()));
                    }
                }

                if (!tempFile.renameTo(targetFile)) {
                    throw new IllegalStateException(String.format(Locale.ENGLISH,
                            "Can't rename the  temp downloaded file(%s) to the target file(%s)",
                            tempPath, targetPath
                    ));
                }
            } finally {
                if (tempFile.exists()) {
                    if (!tempFile.delete()) {
                        LogUtil.E(TAG, String.format(Locale.ENGLISH,
                                "delete the temp file(%s) failed, on completed downloading.",
                                tempPath));
                    }
                }
            }
        }

        private void updateState(int state) {
            updateState(state, null);
        }

        private void updateState(int state, String errorMsg) {
            mRequest.getOrigin().setDownloadState(state);
            mRequest.getOrigin().setErrorMsg(errorMsg);
            mUpdateStateUseCase.execute(UpdateState.Params.updateState(mRequest));

            if (state == DownloadState.STATE_ERROR || state == DownloadState.STATE_CANCEL) {
                mPendingRequests.remove(mRequest.getId());
            }
        }

        private void deleteTaskFiles() {
            deleteTempFile();
            deleteTargetFile();
        }

        private void deleteTempFile() {
            final String tempFilePath
                    = DownloadFileUtil.getTempPath(mRequest.getOrigin().getDestinationPath());

            if (tempFilePath != null) {
                final File tempFile = new File(tempFilePath);
                if (tempFile.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    tempFile.delete();
                }
            }
        }

        private void deleteTargetFile() {
            final String targetFilePath = mRequest.getOrigin().getDestinationPath();
            if (targetFilePath != null) {
                final File targetFile = new File(targetFilePath);
                if (targetFile.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    targetFile.delete();
                }
            }
        }

        private boolean checkPause() {
            return mPaused;
        }

        private void pause() {
            mPaused = true;
        }

        private boolean checkCancel() {
            return mCanceled;
        }

        private void cancel() {
            mCanceled = true;
            mPaused = true;
        }
    }
}
