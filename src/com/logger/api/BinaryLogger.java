package com.logger.api;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public interface BinaryLogger <T extends BinaryLoggable> extends Closeable {
    void write(T loggable) throws IOException;

    Iterator<T> read(File file, Class<T> clazz) throws IOException;
}
