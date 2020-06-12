package com.logger.api.impl;

import com.logger.api.BinaryLoggable;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This Iterator opens stream on the input file and it should be closed(Closeable).
 * It will be closed in any way when BinaryFileLogger is closed or you can do it explicitly.
 *
 * @param <T> extension of BinaryLoggable.
 */
class LazyBinaryFileIterator<T extends BinaryLoggable> implements Iterator<T>, Closeable {
    private final Class<T> clazz;
    private final InputStream inputStream;
    private final ReentrantLock lock = new ReentrantLock();
    private final Queue<T> buffer = new ConcurrentLinkedDeque<>();

    public LazyBinaryFileIterator(File file, Class<T> clazz) throws IOException {
        this.clazz = clazz;
        this.inputStream = Files.newInputStream(file.toPath());
    }

    @Override
    public boolean hasNext() {
        try {
            if (buffer.isEmpty()) {
                tryFillBuffer();
            }
        } catch (Exception e) {
            System.err.println("Error occurred on file iteration: " + e.getMessage() + ". Stopping.");
            try {
                close();
            } catch (IOException ioe) {
                System.err.println("Cannot close: " + ioe.getMessage());
            }
        }
        return !buffer.isEmpty();
    }

    private void tryFillBuffer() throws Exception {
        try {
            lock.lock();
            T next = tryReadNext();
            if (next != null) {
                buffer.add(next);
            }
        } finally {
            lock.unlock();
        }
    }

    private T tryReadNext() throws Exception {
        int objSize;
        T result = null;
        if ((objSize = inputStream.read()) != -1) {
            byte[] objState = new byte[objSize];
            if (inputStream.read(objState, 0, objSize) != -1) {
                T instance = clazz.getDeclaredConstructor().newInstance();
                instance.fromBytes(objState);
                result = instance;
            }
        }
        return result;
    }

    @Override
    public T next() {
        if (hasNext()) {
            return buffer.poll();
        }
        throw new NoSuchElementException("Cannot read anymore");
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
