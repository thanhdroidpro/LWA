package com.kinglloy.download.stream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author jinyalin
 * @since 2017/5/31.
 */

public class DownloadRandomAccessFile implements DownloadOutput {
    private final RandomAccessFile mAccessFile;

    DownloadRandomAccessFile(File file) throws FileNotFoundException {
        mAccessFile = new RandomAccessFile(file, "rw");
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        mAccessFile.write(b, off, len);
    }

    @Override
    public void sync() throws IOException {
        mAccessFile.getFD().sync();
    }

    @Override
    public void close() throws IOException {
        mAccessFile.close();
    }

    @Override
    public void seek(long offset) throws IOException {
        mAccessFile.seek(offset);
    }

    @Override
    public void setLength(long totalBytes) throws IOException {
        mAccessFile.setLength(totalBytes);
    }
}
