package com.kinglloy.download.stream;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author jinyalin
 * @since 2017/5/31.
 */

public class DownloadOutputFactory {
    public static DownloadOutput create(File file) throws FileNotFoundException {
        return new DownloadRandomAccessFile(file);
    }
}
