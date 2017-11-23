package com.kinglloy.download.schedule;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;

import com.kinglloy.download.KinglloyDownloader;
import com.kinglloy.download.KinglloyDownloader.Request;
import com.kinglloy.download.domain.iteractor.AddDownload;
import com.kinglloy.download.domain.iteractor.GetDownload;
import com.kinglloy.download.domain.iteractor.PauseAllDownload;
import com.kinglloy.download.domain.iteractor.QueryDownloadId;
import com.kinglloy.download.exceptions.PermissionException;
import com.kinglloy.download.exceptions.RequestDuplicateException;
import com.kinglloy.download.exceptions.RequestNotExistException;
import com.kinglloy.download.module.InternalRequest;
import com.kinglloy.download.observable.DownloadObservableImpl;
import com.kinglloy.download.state.DownloadState;
import com.kinglloy.download.utils.DownloadFileUtil;
import com.kinglloy.download.utils.LogUtil;

import java.io.File;
import java.security.InvalidParameterException;

/**
 * @author jinyalin
 * @since 2017/5/27.
 */

public class DownloadRequestHunter implements Handler.Callback {
    private static final String TAG = "DownloadTaskHunter";

    private static final class InstanceHolder {
        private static final DownloadRequestHunter INSTANCE = new DownloadRequestHunter();
    }

    // init environment
    private static final int MSG_PREPARE_ENV = 1;

    // createDbStore and start
    private static final int MSG_CHECK_REQUEST = 11;
    private static final int MSG_PREPARE_REQUEST = 12;
    private static final int MSG_RESUME_REQUEST = 13;
    private static final int MSG_LAUNCH_REQUEST = 14;

    // pause and start
    private static final int MSG_PAUSE_REQUEST = 21;
    private static final int MSG_PAUSE_REQUESTS = 22;
    private static final int MSG_START_REQUEST = 23;
    private static final int MSG_START_REQUESTS = 24;

    // cancel
    private static final int MSG_CANCEL_REQUEST = 31;
    private static final int MSG_CANCEL_REQUESTS = 32;

    private final Handler handler;
    private HandlerThread handlerThread;

    private DownloadWorker worker;

    // db use case
    private AddDownload addDownloadUseCase;
    private GetDownload getDownloadUseCase;
    private QueryDownloadId queryDownloadIdUseCase;
    private PauseAllDownload pauseAllDownloadUseCase;

    public static DownloadRequestHunter getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private DownloadRequestHunter() {
        handlerThread = new PriorityHandlerThread("DownloadRequestHunter:Handler",
                Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), this);

        worker = DownloadWorker.getInstance();

        addDownloadUseCase = new AddDownload();
        getDownloadUseCase = new GetDownload();
        queryDownloadIdUseCase = new QueryDownloadId();
        pauseAllDownloadUseCase = new PauseAllDownload();

        handler.sendEmptyMessage(MSG_PREPARE_ENV);
    }

    public long postRequest(Request request) {
        maybeFixDestinationPath(request);
        IdRequest idRequest;

        idRequest = new IdRequest(request);
        handler.obtainMessage(MSG_CHECK_REQUEST, idRequest).sendToTarget();
        return idRequest.getId();
    }

    public long queryId(Request request) {
        maybeFixDestinationPath(request);
        return queryDownloadIdUseCase.execute(QueryDownloadId.Params.queryId(request));
    }

    public long queryDownloadedSize(long downloadId) {
        InternalRequest request =
                getDownloadUseCase.execute(GetDownload.Params.getRequest(downloadId));
        if (request == null) {
            return -1;
        } else {
            return request.getDownloadedSize();
        }
    }

    public long queryTotalSize(long downloadId) {
        InternalRequest request =
                getDownloadUseCase.execute(GetDownload.Params.getRequest(downloadId));
        if (request == null) {
            return -1;
        } else {
            return request.getTotalSize();
        }
    }


    public void startRequest(long id) {
        handler.obtainMessage(MSG_START_REQUEST, id).sendToTarget();
    }

    public void startRequest(long[] ids) {
        handler.obtainMessage(MSG_START_REQUESTS, ids).sendToTarget();
    }

    public void pauseRequest(long id) {
        handler.obtainMessage(MSG_PAUSE_REQUEST, id).sendToTarget();
    }

    public void pauseRequest(long[] ids) {
        handler.obtainMessage(MSG_PAUSE_REQUESTS, ids).sendToTarget();
    }

    public void cancelRequest(long id) {
        handler.obtainMessage(MSG_CANCEL_REQUEST, id).sendToTarget();
    }

    public void cancelRequest(long[] id) {
        handler.obtainMessage(MSG_CANCEL_REQUESTS, id).sendToTarget();
    }

    public int getState(long id) {
        boolean prepareEnv = handler.hasMessages(MSG_PREPARE_ENV);
        InternalRequest request = getDownloadUseCase.execute(GetDownload.Params.getRequest(id));
        if (request == null) {
            return DownloadState.STATE_UNKNOWN;
        } else {
            if (prepareEnv && (request.getDownloadState() == DownloadState.STATE_DOWNLOADING
                    || request.getDownloadState() == DownloadState.STATE_PENDING)) {
                request.setDownloadState(DownloadState.STATE_PAUSE);
            }
            return request.getDownloadState();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        try {
            switch (msg.what) {
                case MSG_PREPARE_ENV: {
                    pauseAllDownloadUseCase.execute(null);
                    return true;
                }
                case MSG_CHECK_REQUEST: {
                    IdRequest request = (IdRequest) msg.obj;
                    onCheckRequest(request);
                    handler.obtainMessage(MSG_PREPARE_REQUEST, request).sendToTarget();
                    return true;
                }
                case MSG_PREPARE_REQUEST: {
                    IdRequest request = (IdRequest) msg.obj;
                    onPrepareRequest(request);
                    handler.obtainMessage(MSG_RESUME_REQUEST, request).sendToTarget();
                    return true;
                }
                case MSG_RESUME_REQUEST: {
                    IdRequest request = (IdRequest) msg.obj;
                    onResumeRequest(request);
                    handler.obtainMessage(MSG_LAUNCH_REQUEST, request).sendToTarget();
                    return true;
                }
                case MSG_LAUNCH_REQUEST: {
                    IdRequest request = (IdRequest) msg.obj;
                    onLaunchRequest(request);
                    return true;
                }
                case MSG_PAUSE_REQUEST: {
                    long id = (long) msg.obj;
                    onPauseRequest(id);
                    return true;
                }
                case MSG_PAUSE_REQUESTS: {
                    long[] ids = (long[]) msg.obj;
                    onPauseRequests(ids);
                    return true;
                }
                case MSG_START_REQUEST: {
                    long id = (long) msg.obj;
                    onStartRequest(id);
                    return true;
                }
                case MSG_START_REQUESTS: {
                    long[] ids = (long[]) msg.obj;
                    onStartRequests(ids);
                    return true;
                }
                case MSG_CANCEL_REQUEST: {
                    long id = (long) msg.obj;
                    onCancelRequest(id);
                    return true;
                }
                case MSG_CANCEL_REQUESTS: {
                    long[] ids = (long[]) msg.obj;
                    onCancelRequests(ids);
                    return true;
                }
            }
        } catch (Throwable e) {
            onError(msg, e);
        }
        return false;
    }

    private void maybeFixDestinationPath(Request request) {
        if (TextUtils.isEmpty(request.getDestinationPath())) {
            request.setDestinationPath(DownloadFileUtil
                    .getDefaultSaveFilePath(request.getUri().toString()));
            LogUtil.D(TAG, "save Path is null to " + request.getDestinationPath());
        }
    }

    private void onCheckRequest(IdRequest request)
            throws PermissionException {
//        checkExternalPermission();
    }

    // handler thread
    private void onPrepareRequest(IdRequest request) {
        Request origin = request.getOrigin();

        final String fileDirString = DownloadFileUtil.getParent(origin.getDestinationPath());
        if (fileDirString == null) {
            throw new InvalidParameterException(
                    DownloadFileUtil.formatString("the provided mPath[%s] is invalid," +
                            " can't find its directory", origin.getDestinationPath()));
        }
        final File dir = new File(fileDirString);

        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
    }

    private void onResumeRequest(IdRequest request) {
        InternalRequest sizeRequest =
                getDownloadUseCase.execute(GetDownload.Params.getRequest(request.getId()));
        if (sizeRequest != null) {
            request.updateRequest(sizeRequest);
            LogUtil.D(TAG, "Request already exist. so start it. downloaded size = "
                    + sizeRequest.getDownloadedSize()
                    + " total size  = " + sizeRequest.getTotalSize());
        } else {

            LogUtil.D(TAG, "Request have not exist. insert it to db");
            addDownloadUseCase.execute(AddDownload.Params.addRequest(request));
        }
    }

    private void onLaunchRequest(IdRequest request) throws RequestDuplicateException {
        worker.execute(request);
    }

    private void onPauseRequest(long id) throws RequestNotExistException {
        InternalRequest request = getDownloadUseCase.execute(GetDownload.Params.getRequest(id));
        if (request == null) {
            throw new RequestNotExistException(id,
                    "Request " + id + " is not exist, you cannot start it.");
        }
        worker.pause(id);
    }

    private void onPauseRequests(long[] ids) {
        for (long id : ids) {
            try {
                onPauseRequest(id);
            } catch (Throwable e) {
                onError(handler.obtainMessage(MSG_PAUSE_REQUESTS), e);
            }
        }
    }

    private void onStartRequest(long id)
            throws RequestNotExistException, RequestDuplicateException {
        InternalRequest request = getDownloadUseCase.execute(GetDownload.Params.getRequest(id));
        if (request == null) {
            throw new RequestNotExistException(id,
                    "Request " + id + " is not exist, you cannot start it.");
        }
        worker.start(new IdRequest(request));
    }

    private void onStartRequests(long[] ids) {
        for (long id : ids) {
            try {
                onStartRequest(id);
            } catch (Throwable e) {
                onError(handler.obtainMessage(MSG_START_REQUESTS), e);
            }
        }
    }

    private void onCancelRequest(long id)
            throws RequestNotExistException {
        InternalRequest request = getDownloadUseCase.execute(GetDownload.Params.getRequest(id));
        if (request == null) {
            throw new RequestNotExistException(id,
                    "Request " + id + " is not exist, you cannot start it.");
        }
        worker.cancel(id);
    }

    private void onCancelRequests(long[] ids) {
        for (long id : ids) {
            try {
                onCancelRequest(id);
            } catch (Throwable e) {
                onError(handler.obtainMessage(MSG_CANCEL_REQUESTS), e);
            }
        }
    }

    private void checkExternalPermission() throws PermissionException {
        if (Build.VERSION.SDK_INT >= 23 && KinglloyDownloader.getContext()
                .checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            throw new PermissionException("You don't have external storage permission.");
        }
    }

    private void onError(Message msg, Throwable error) {
        LogUtil.E(TAG, "Handle message : " + msg.what + " error.", error);
        switch (msg.what) {
            case MSG_CHECK_REQUEST: {
                if (error instanceof PermissionException) {
                    DownloadObservableImpl.getInstance()
                            .notifyError((IdRequest) msg.obj, 0, error.getMessage());
                }
                break;
            }
            case MSG_PREPARE_REQUEST: {
                if (error instanceof InvalidParameterException) {
                    DownloadObservableImpl.getInstance()
                            .notifyError((IdRequest) msg.obj, 0, error.getMessage());
                }
                break;
            }
            case MSG_LAUNCH_REQUEST: {
                if (error instanceof RequestDuplicateException) {
                    DownloadObservableImpl.getInstance()
                            .notifyError((IdRequest) msg.obj, 0, error.getMessage());
                }
                break;
            }
            case MSG_PAUSE_REQUEST:
            case MSG_PAUSE_REQUESTS: {
                if (error instanceof RequestNotExistException) {
                    DownloadObservableImpl.getInstance()
                            .notifyError(((RequestNotExistException) error).getDownloadId(),
                                    0, error.getMessage());
                }
                break;
            }
            case MSG_START_REQUEST:
            case MSG_START_REQUESTS: {
                if (error instanceof RequestNotExistException) {
                    DownloadObservableImpl.getInstance()
                            .notifyError(((RequestNotExistException) error).getDownloadId(),
                                    0, error.getMessage());
                } else if (error instanceof RequestDuplicateException) {
                    DownloadObservableImpl.getInstance()
                            .notifyError(((RequestDuplicateException) error).getDownloadId(),
                                    0, error.getMessage());
                }
                break;
            }
        }
    }
}
