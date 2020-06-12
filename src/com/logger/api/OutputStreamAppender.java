package com.logger.api;

import java.io.Closeable;
import java.io.IOException;

public interface OutputStreamAppender<T extends BinaryLoggable> extends Closeable {
    void append(T event) throws IOException;
}
