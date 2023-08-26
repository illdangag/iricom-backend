package com.illdangag.iricom.storage.data;

import com.illdangag.iricom.storage.data.entity.FileMetadata;
import com.illdangag.iricom.storage.data.response.FileMetadataInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IricomFileInputStream extends InputStream {

    private final InputStream inputStream;

    private final FileMetadata fileMetadata;

    public IricomFileInputStream(InputStream inputStream, FileMetadata fileMetadata) {
        this.inputStream = inputStream;
        this.fileMetadata = fileMetadata;
    }

    public FileMetadataInfo getFileMetadataInfo() {
        return new FileMetadataInfo(this.fileMetadata);
    }

    @Override
    public int read() throws IOException {
        return this.inputStream.read();
    }

    @Override
    public int available() throws IOException {
        return this.inputStream.available();
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return this.inputStream.markSupported();
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        return this.inputStream.transferTo(out);
    }
}
