package br.com.zalf.prolog.webservice.commons.util;

import br.com.zalf.prolog.webservice.BuildConfig;
import io.sentry.Sentry;
import io.sentry.event.Event;
import io.sentry.event.EventBuilder;
import org.apache.logging.log4j.LogManager;

public class Log {
	
	public static void d(String tag, String message) {
		if (BuildConfig.DEBUG) {
			LogManager.getLogger(tag).debug(message);
		}
	}

	public static void w(String tag, String message) {
		LogManager.getLogger(tag).warn(message);
	}
	
	public static void e(String tag, String message) {
		LogManager.getLogger(tag).error(message);
		if (!BuildConfig.DEBUG) {
			Sentry.capture(message);
		}
	}
	
	public static void e(String tag, String message, Throwable t) {
		LogManager.getLogger(tag).error(message, t);
		if (!BuildConfig.DEBUG) {
			Sentry.capture(t);
		}
	}
}
