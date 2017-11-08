package com.kinglloy.download.schedule;

import com.kinglloy.download.KinglloyDownloader;
import com.kinglloy.download.module.InternalRequest;

/**
 * @author jinyalin
 * @since 2017/5/28.
 */

public class IdRequest {
    private long mId;
    private InternalRequest mRequest;

    public IdRequest(KinglloyDownloader.Request request) {
        this.mRequest = new InternalRequest(request);
        mId = request.hashCode();
        if (mId < 0) {
            mId *= -1;
        }
    }

    void updateRequest(InternalRequest request) {
        this.mRequest = request;
    }

    public long getId() {
        return mId;
    }

    public InternalRequest getOrigin() {
        return mRequest;
    }
}
