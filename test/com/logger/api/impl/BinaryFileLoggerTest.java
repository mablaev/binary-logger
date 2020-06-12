package com.logger.api.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BinaryFileLoggerTest {

    private final String pathToFile = "events.bin";

    @Before
    public void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(pathToFile));
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(pathToFile));
    }

    @Test
    public void simpleWriteSucceed() throws IOException {
        try (BinaryFileLogger<Event> logger = new BinaryFileLogger<>(pathToFile)) {
            Instant now = Instant.now();
            Event evt = Event.of(now, "test");
            logger.write(evt);

            Iterator<Event> eventIterator = logger.read(new File(pathToFile), Event.class);
            assertTrue(eventIterator.hasNext());
            Event actual = eventIterator.next();
            assertEquals(now, actual.getTimestamp());
            assertEquals("test", actual.getMessage());
            assertFalse(eventIterator.hasNext());
        }
    }
}