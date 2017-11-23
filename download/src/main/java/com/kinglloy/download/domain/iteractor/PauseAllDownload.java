package com.kinglloy.download.domain.iteractor;

import com.kinglloy.download.data.repository.DownloadRequestRepository;
import com.kinglloy.download.domain.DownloadRepository;

/**
 * @author jinyalin
 * @since 2017/5/31.
 */

public class PauseAllDownload extends UseCase<Void, Void> {
    private DownloadRepository repository = DownloadRequestRepository.getInstance();

    @Override
    public Void execute(Void aVoid) {
        repository.pauseAll();
        return null;
    }
}
