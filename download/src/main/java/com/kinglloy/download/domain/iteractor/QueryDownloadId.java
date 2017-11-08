package com.kinglloy.download.domain.iteractor;

import com.kinglloy.download.KinglloyDownloader.Request;
import com.kinglloy.download.data.repository.DownloadRequestRepository;
import com.kinglloy.download.domain.DownloadRepository;

/**
 * @author jinyalin
 * @since 2017/5/28.
 */

public class QueryDownloadId extends UseCase<Long, QueryDownloadId.Params> {
    private DownloadRepository repository = DownloadRequestRepository.getInstance();

    @Override
    public Long execute(Params params) {
        return repository.queryDownloadId(params.request);
    }

    public static class Params {
        private Request request;

        private Params(Request request) {
            this.request = request;
        }

        public static Params queryId(Request request) {
            return new Params(request);
        }
    }
}
