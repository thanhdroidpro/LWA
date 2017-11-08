package com.kinglloy.download.domain.iteractor;

import com.kinglloy.download.data.repository.DownloadRequestRepository;
import com.kinglloy.download.domain.DownloadRepository;
import com.kinglloy.download.module.InternalRequest;

/**
 * @author jinyalin
 * @since 2017/5/28.
 */

public class GetDownload extends UseCase<InternalRequest, GetDownload.Params> {

    private DownloadRepository repository = DownloadRequestRepository.getInstance();

    @Override
    public InternalRequest execute(Params params) {
        return repository.getDownload(params.downloadId);
    }

    public static class Params {
        private long downloadId;

        private Params(long downloadId) {
            this.downloadId = downloadId;
        }

        public static Params getRequest(long downloadId) {
            return new Params(downloadId);
        }
    }
}
