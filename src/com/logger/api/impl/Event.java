package com.logger.api.impl;

import com.logger.api.BinaryLoggable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;

public final class Event implements BinaryLoggable {
    private Instant timestamp;
    private String message;

    public Event() {
    }

    private Event(Instant timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    public static Event of(Instant timestamp, String message) {
        return new Event(timestamp, message);
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public byte[] toBytes() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dataOutput = new DataOutputStream(bos)) {
            dataOutput.writeLong(Converters.INSTANT_TO_LONG.apply(timestamp));
            dataOutput.writeUTF(message);
            return bos.toByteArray();
        }
    }

    @Override
    public void fromBytes(byte[] rawBytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(rawBytes);
             DataInputStream dataInput = new DataInputStream(bis)) {
            this.timestamp = Converters.LONG_TO_INSTANT.apply(dataInput.readLong());
            this.message = dataInput.readUTF();
        }
    }

    @Override
    public String toString() {
        return "Event{" +
                "timestamp=" + timestamp +
                ", message='" + message + '\'' +
                '}';
    }
}
