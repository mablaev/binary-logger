package com.logger.api.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileSizeRollingPolicyTest {
    private FileSizeRollingPolicy instance;
    private final String pathToFile = "test.bin";

    @Before
    public void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(pathToFile));
        instance = new FileSizeRollingPolicy(pathToFile, 100L);
    }

    @After
    public void tearDown() throws Exception {
        Files.deleteIfExists(Paths.get(pathToFile));
    }

    @Test
    public void rolloverSucceed() throws IOException {

        assertEquals(pathToFile, instance.getCurrentFile().getName());
        for (int i = 0; i < 100; i++) {
            Files.writeString(Paths.get(pathToFile), UUID.randomUUID().toString(), CREATE, APPEND);
        }
        assertTrue(instance.tryRollover());
        assertEquals("test1.bin", instance.getCurrentFile().getName());
    }

    @Test
    public void lengthIsLessSkipRollover() throws IOException {
        assertEquals(pathToFile, instance.getCurrentFile().getName());
        for (int i = 0; i < 2; i++) {
            Files.writeString(Paths.get(pathToFile), UUID.randomUUID().toString(), CREATE, APPEND);
        }
        assertFalse(instance.tryRollover());
        assertEquals("test.bin", instance.getCurrentFile().getName());
    }
}