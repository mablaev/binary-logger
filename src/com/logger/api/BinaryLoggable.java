package com.logger.api;

import java.io.IOException;

public interface BinaryLoggable {
    byte[] toBytes() throws IOException;

    void fromBytes(byte[] rawBytes) throws IOException;
}
