package com.logger.api.impl;

import com.logger.api.BinaryLoggable;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EagerBinaryFileIterator<T extends BinaryLoggable> implements Iterator<T> {
    private final Queue<T> buffer = new ConcurrentLinkedQueue<>();

    public EagerBinaryFileIterator(File file, Class<T> clazz) throws IOException {
        this(new LazyBinaryFileIterator<>(file, clazz));
    }

    public EagerBinaryFileIterator(LazyBinaryFileIterator<T> iterator) throws IOException {
        try (iterator) {
            iterator.forEachRemaining(buffer::add);
        }
    }

    @Override
    public boolean hasNext() {
        return !buffer.isEmpty();
    }

    @Override
    public T next() {
        return buffer.poll();
    }
}
