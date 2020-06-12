package com.logger.api.impl;

import com.logger.api.BinaryLoggable;
import com.logger.api.OutputStreamAppender;
import com.logger.api.RollingPolicy;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class BinaryFileOutputStreamAppender<T extends BinaryLoggable> implements OutputStreamAppender<T> {
    private OutputStream outputStream;
    private final RollingPolicy rollingPolicy;
    private final Lock lock = new ReentrantLock();

    public BinaryFileOutputStreamAppender(RollingPolicy rollingPolicy) throws IOException {
        this.rollingPolicy = rollingPolicy;
        this.outputStream = Files.newOutputStream(rollingPolicy.getCurrentFile().toPath(), CREATE, APPEND);
    }

    @Override
    public void append(T event) throws IOException {
        writeBytes(event.toBytes());
    }

    private void writeBytes(byte[] outBytes) throws IOException {
        if (outBytes != null && outBytes.length > 0) {
            lock.lock();
            try {
                tryRollover();

                outputStream.write(outBytes.length);
                outputStream.write(outBytes);
                outputStream.flush();
            } finally {
                lock.unlock();
            }
        }
    }

    private void tryRollover() throws IOException {
        if (rollingPolicy.tryRollover()) {
            this.outputStream.close();
            this.outputStream = Files.newOutputStream(rollingPolicy.getCurrentFile().toPath(), CREATE, APPEND);
        }
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
        outputStream = null;
    }
}
