package br.com.zalf.prolog.webservice.commons.util;

import org.apache.logging.log4j.LogManager;

public class Log {
    private Log() {
        throw new IllegalStateException(Log.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void d(final String tag, final String message) {
        if (PrologUtils.isDebug()) {
            LogManager.getLogger(tag).debug(message);
        }
    }

    public static void i(final String tag, final String message) {
        LogManager.getLogger(tag).info(message);
    }

    public static void w(final String tag, final String message) {
        LogManager.getLogger(tag).warn(message);
    }

    public static void e(final String tag, final String message) {
        LogManager.getLogger(tag).error(message);
    }

    public static void e(final String tag, final String message, final Throwable t) {
        LogManager.getLogger(tag).error(message, t);
    }
}
