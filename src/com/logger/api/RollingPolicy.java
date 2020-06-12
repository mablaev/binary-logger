package com.logger.api;

import java.io.File;

public interface RollingPolicy {
    /**
     * Take the current logged file.
     *
     * @return Current file is being processed.
     */
    File getCurrentFile();

    /**
     * Tries to roll over current file if we exceed condition of this policy.
     *
     * @return true if file was rolled over.
     */
    boolean tryRollover();
}
