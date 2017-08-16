package br.com.zalf.prolog.webservice.commons.util;

import br.com.zalf.prolog.webservice.BuildConfig;
import org.apache.logging.log4j.LogManager;

public class L {
	
	public static void d(String tag, String message) {
		if (BuildConfig.DEBUG) {
			LogManager.getLogger(tag).debug(message);
		}
	}
	
	public static void e(String tag, String message) {
		LogManager.getLogger(tag).error(message);
	}
	
	public static void e(String tag, String message, Throwable t) {
		LogManager.getLogger(tag).error(message, t);
	}
}
