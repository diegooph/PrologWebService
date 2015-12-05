package br.com.empresa.oprojeto.webservice.util;

import java.sql.Timestamp;
import java.util.Date;

public class DateUtil {
	
	public static Timestamp toTimestamp(Date utilDate) {
		return new Timestamp(utilDate.getTime());
	}
	
	public static java.sql.Date toSqlDate(Date utilDate) {
		return new java.sql.Date(utilDate.getTime());
	}
}
