package com.logger;

import com.logger.api.BinaryLogger;
import com.logger.api.impl.BinaryFileLogger;
import com.logger.api.impl.Event;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        // write your code here
        try {
            Args programArgs = Args.of(args);

            if (Args.NON_VALID_ARGS.equals(programArgs)) {
                usage();
            } else {
                System.out.println("Program is started...");
                if (programArgs.getConcurrencyLevel() > Runtime.getRuntime().availableProcessors()) {
                    System.out.println("WARN: Number of available processors is less than thread count.");
                }
                doWith(programArgs);
            }

        } catch (InterruptedException e) {
            System.err.println("[WARN] Program was unexpectedly interrupted " + e);
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("Done.");
        }
    }

    private static void doWith(Args programArgs) throws InterruptedException {
        if (programArgs.shouldWrite()) {
            writeEvents(programArgs);
        } else {
            System.out.println("Skipping write.");
        }
        if (programArgs.shouldRead()) {
            readAndPrintEvents(programArgs);
        } else {
            System.out.println("Skipping read.");
        }
    }

    private static void readAndPrintEvents(Args args) {
        try (BinaryLogger<Event> logger = new BinaryFileLogger<>(args.getPathToFile())) {
            Iterator<Event> eventIterator = logger.read(new File(args.getPathToFile()), Event.class);
            while (eventIterator.hasNext()) {
                System.out.println("Read next event: " + eventIterator.next());
            }
        } catch (IOException e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }

    private static void writeEvents(Args args) throws InterruptedException {
        System.out.println("Concurrency level is " + args.getConcurrencyLevel());
        List<Event> events = generateEvents(args.getNumberOfEvents());
        try (BinaryLogger<Event> logger = new BinaryFileLogger<>(args.getPathToFile(), args.getMaxFileSize())) {
            if (args.getConcurrencyLevel() > 1 && args.getConcurrencyLevel() < events.size()) {
                writeConcurrently(logger, events, args.getConcurrencyLevel());
            } else {
                writeAll(events, logger);
            }
        } catch (IOException e) {
            System.err.println("Error occurred " + e.getMessage());
        }
    }

    private static void writeAll(List<Event> events, BinaryLogger<Event> logger) {
        for (Event event : events) {
            try {
                logger.write(event);
            } catch (IOException e) {
                System.err.println("Was not able to write next event " + e.getMessage());
            }
        }
    }

    private static void writeConcurrently(BinaryLogger<Event> logger, List<Event> events, int concurrencyLevel) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(concurrencyLevel);
        int chunkSize = events.size() / concurrencyLevel;

        CompletableFuture[] completableFutures = IntStream.rangeClosed(0, concurrencyLevel)
                .mapToObj(idx -> events.subList(idx * chunkSize, Math.min(events.size(), (idx + 1) * chunkSize)))
                .filter(evts -> evts.size() > 0)
                .map(evts -> CompletableFuture.runAsync(() -> writeAll(evts, logger), executor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(completableFutures).join();
        executor.shutdown();
        if (!executor.awaitTermination(60000, TimeUnit.SECONDS)) {
            System.err.println("Threads didn't finish in 60000 seconds!");
        }
    }

    private static List<Event> generateEvents(int numberOfEvents) {
        System.out.println("Generating randomly " + numberOfEvents + " events.");
        return IntStream.range(0, numberOfEvents)
                .mapToObj(idx -> Event.of(Instant.now(), UUID.randomUUID().toString()))
                .collect(Collectors.toList());
    }

    private static void usage() {
        System.out.println("Parameter list you should choose:\n" +
                "f=<some path or file name>\n" +
                "fsz=<max file size>\n" +
                "evts=<number of events>\n" +
                "cl=<concurrency level>\n" +
                "wr=<true/false, omit means false>\n" +
                "rd=<true/false, omit means false>"
        );
    }
}
