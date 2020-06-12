package com.logger.api.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

public interface Converters {
    Function<Instant, Long> INSTANT_TO_LONG = input -> input != null ? ChronoUnit.MICROS.between(Instant.EPOCH, input) : 0;
    Function<Long, Instant> LONG_TO_INSTANT = input -> input > 0 ? Instant.EPOCH.plus(input, ChronoUnit.MICROS) : null;
}
