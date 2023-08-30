package com.wb2code.microbox.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * @author lwp
 * @date 2022-11-17
 */
public class LogUtil {
    private static final Logger log = LoggerFactory.getLogger(LogUtil.class);

    /**
     * @param format
     * @param arguments
     */
    public static void info(String format, Object... arguments) {
        CompletableFuture.runAsync(() -> {
            log.info("MICROLOG-" + format, arguments);
        });
    }

    /**
     * @param format
     * @param arguments
     */
    public static void error(String format, Object... arguments) {
        CompletableFuture.runAsync(() -> {
            log.error("MICROLOG-" + format, arguments);
        });
    }

    /**
     * @param format
     * @param arguments
     */
    public static void debug(String format, Object... arguments) {
        CompletableFuture.runAsync(() -> {
            log.debug("MICROLOG-" + format, arguments);
        });
    }
}
