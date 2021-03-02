package br.com.zalf.prolog.webservice.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {

    private Log() {
        throw new IllegalStateException(Log.class.getSimpleName() + " cannot be instantiated!");
    }

    private static Logger getLogger(final String tag) {
        return LoggerFactory.getLogger(tag);
    }

    public static void d(final String tag, final String message) {
        getLogger(tag).debug(message);
    }

    public static void i(final String tag, final String message) {
        getLogger(tag).info(message);
    }

    public static void w(final String tag, final String message) {
        getLogger(tag).warn(message);
    }

    public static void e(final String tag, final String message) {
        getLogger(tag).error(message);
    }

    public static void e(final String tag, final String message, final Throwable t) {
        getLogger(tag).error(message, t);
    }
}
