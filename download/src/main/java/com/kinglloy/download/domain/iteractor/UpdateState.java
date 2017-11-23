package com.kinglloy.download.domain.iteractor;

import com.kinglloy.download.data.repository.DownloadRequestRepository;
import com.kinglloy.download.domain.DownloadRepository;
import com.kinglloy.download.schedule.IdRequest;

/**
 * @author jinyalin
 * @since 2017/5/31.
 */

public class UpdateState extends UseCase<Void, UpdateState.Params> {
    private DownloadRepository repository = DownloadRequestRepository.getInstance();

    @Override
    public Void execute(Params params) {
        repository.updateState(params.request);
        return null;
    }

    public static class Params {
        private IdRequest request;

        private Params(IdRequest request) {
            this.request = request;
        }

        public static Params updateState(IdRequest request) {
            return new Params(request);
        }
    }
}
