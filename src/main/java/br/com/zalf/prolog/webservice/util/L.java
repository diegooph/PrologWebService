package br.com.zalf.prolog.webservice.util;

import br.com.zalf.prolog.webservice.BuildConfig;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class L {
	
	public static void d(String tag, String message) {
		if (BuildConfig.DEBUG) {
			Logger.getLogger(tag).log(Level.DEBUG, message);
		}
	}
	
	public static void e(String tag, String message) {
		Logger.getLogger(tag).error(message);
	}
	
	public static void e(String tag, String message, Throwable t) {
		Logger.getLogger(tag).error(message, t);
	}
}
