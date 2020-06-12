package com.logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

final class Args {
    public static final Args NON_VALID_ARGS = new Args();

    private Map<String, String> arguments;

    private Args() {
        //no code
    }

    public static Args of(String[] args) {
        Args progArgs = new Args();
        Map<String, String> argMaps = new HashMap<>();
        for (String arg : args) {
            String[] parts = arg.split("=");
            if (parts.length == 2) {
                argMaps.put(parts[0], parts[1]);
            } else {
                System.err.println("Error parameter is detected: " + arg);
                return NON_VALID_ARGS;
            }
        }
        progArgs.arguments = Collections.unmodifiableMap(argMaps);
        if (progArgs.valid()) {
            return progArgs;
        } else {
            return NON_VALID_ARGS;
        }
    }

    private boolean valid() {
        if (getPathToFile() == null || "".equals(getPathToFile().trim())) {
            return false;
        }
        return true;
    }

    public int getConcurrencyLevel() {
        return readSafely("cl", Integer::parseInt, 1);
    }

    public long getMaxFileSize() {
        return readSafely("fsz", Long::parseLong, 1024L);
    }

    public int getNumberOfEvents() {
        return readSafely("evts", Integer::parseInt, 10);
    }

    public boolean shouldWrite() {
        return Boolean.valueOf(arguments.get("wr")) == Boolean.TRUE;
    }

    public boolean shouldRead() {
        return Boolean.valueOf(arguments.get("rd")) == Boolean.TRUE;
    }

    public String getPathToFile() {
        return arguments.get("f");
    }

    private <T> T readSafely(String paramKey, Function<String, T> converter, T defaultVal) {
        try {
            return converter.apply(arguments.getOrDefault(paramKey, defaultVal.toString()));
        } catch (Exception e) {
            System.err.println("Cannot parse argument: " + e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }
}