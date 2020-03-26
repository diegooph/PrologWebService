package br.com.zalf.prolog.webservice.commons.util;

import br.com.zalf.prolog.webservice.config.BuildConfig;
import io.sentry.Sentry;
import org.apache.logging.log4j.LogManager;

public class Log {
	
	public static void d(final String tag, final String message) {
		if (BuildConfig.DEBUG) {
			LogManager.getLogger(tag).debug(message);
		}
	}

	public static void w(final String tag, final String message) {
		LogManager.getLogger(tag).warn(message);
	}
	
	public static void e(final String tag, final String message) {
		LogManager.getLogger(tag).error(message);
		if (!BuildConfig.DEBUG) {
			Sentry.capture(message);
		}
	}
	
	public static void e(final String tag, final String message, final Throwable t) {
		LogManager.getLogger(tag).error(message, t);
		if (!BuildConfig.DEBUG) {
			Sentry.capture(t);
		}
	}
}
