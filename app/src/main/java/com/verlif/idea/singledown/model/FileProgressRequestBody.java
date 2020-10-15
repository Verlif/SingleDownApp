package com.verlif.idea.singledown.model;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class FileProgressRequestBody extends RequestBody {

    public interface UploadProgressListener {
        void transferred( long size );
    }

    public static final int SEGMENT_SIZE = 2048;

    protected File file;
    protected UploadProgressListener listener;
    protected String contentType;

    public FileProgressRequestBody(File file, String contentType, UploadProgressListener listener) {
        this.file = file;
        this.contentType = contentType;
        this.listener = listener;
    }

    public FileProgressRequestBody() {}

    @Override
    public long contentLength() {
        return file.length();
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(contentType);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(file);
            long total = 0;
            long read;

            while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                total += read;
                sink.flush();
                this.listener.transferred(total);

            }
        } finally {
            Util.closeQuietly(source);
        }
    }

}