package com.logger.api.impl;

import com.logger.api.BinaryLoggable;
import com.logger.api.BinaryLogger;
import com.logger.api.OutputStreamAppender;
import com.logger.api.RollingPolicy;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//TODO: this needs to be refactored. Read and write responsibility should be separated
public class BinaryFileLogger<T extends BinaryLoggable> implements BinaryLogger<T> {
    private final OutputStreamAppender<T> outputStreamAppender;
    private final List<LazyBinaryFileIterator<T>> fileIterators = new CopyOnWriteArrayList<>();

    public BinaryFileLogger(String pathToFile) throws IOException {
        RollingPolicy rollingPolicy = new FileSizeRollingPolicy(pathToFile);
        this.outputStreamAppender = new BinaryFileOutputStreamAppender<>(rollingPolicy);
    }

    public BinaryFileLogger(String pathToFile, long maxFileSize) throws IOException {
        RollingPolicy rollingPolicy = new FileSizeRollingPolicy(pathToFile, maxFileSize);
        this.outputStreamAppender = new BinaryFileOutputStreamAppender<>(rollingPolicy);
    }

    @Override
    public Iterator<T> read(File file, Class<T> clazz) throws IOException {
        LazyBinaryFileIterator<T> fileIterator = new LazyBinaryFileIterator<>(file, clazz);
        fileIterators.add(fileIterator);
        return fileIterator;
    }

    @Override
    public void write(T loggable) throws IOException {
        System.out.println(String.format("[%s] - writing the next object [%s]", Thread.currentThread().getName(), loggable));
        outputStreamAppender.append(loggable);
    }

    @Override
    public void close() throws IOException {
        try {
            outputStreamAppender.close();
        } finally {
            fileIterators.forEach(this::closeIterator);
        }
    }

    private void closeIterator(LazyBinaryFileIterator<T> fi) {
        try {
            fi.close();
        } catch (IOException e) {
            System.err.println("Error occurred during file iterator close: " + e.getMessage());
        }
    }

}
