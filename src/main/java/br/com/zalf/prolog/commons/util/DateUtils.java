package br.com.zalf.prolog.commons.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	/**
	 * Converte uma data no formato java.util.Date para java.sql.Timestamp
	 *
	 * @param utilDate
	 * @return java.sql.Timestamp
	 */
	public static Timestamp toTimestamp(Date utilDate) {
		return new Timestamp(utilDate.getTime());
	}

	/**
	 * Converte uma data no formato java.util.Date para java.sql.Date
	 *
	 * @param utilDate
	 * @return java.sql.Date
	 */
	public static java.sql.Date toSqlDate(Date utilDate) {
		return new java.sql.Date(utilDate.getTime());
	}

	/**
	 * Converte um java.time.LocalDate para java.sql.Date
	 *
	 * @param localDate
	 * @return java.sql.Date
	 */
	public static java.sql.Date toSqlDate(LocalDate localDate) {
		return java.sql.Date.valueOf(localDate);
	}

	/**
	 * Converte um java.sql.Date para java.time.LocalDate
	 *
	 * @param sqlDate
	 * @return java.time.LocalDate
	 */
	public static LocalDate toLocalDate(java.sql.Date sqlDate) {
		return sqlDate.toLocalDate();
	}

	/**
	 * Converte um java.util.date para LocalDateTime
	 * @param date
	 * @return
     */
	public static LocalDateTime toLocalDateTime(Date date){
		Instant instant = Instant.ofEpochMilli(date.getTime());
		LocalDateTime res = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		return res;
	}

	/**
	 * Converte um java.util.Date para java.time.LocalDate
	 *
	 * @param utilDate
	 * @return
	 */
	public static LocalDate toLocalDate(Date utilDate) {
		return toSqlDate(utilDate).toLocalDate();
	}

	/**
	 * Retorna um date com o primerio dia do mes inserido
	 * @param date
	 * @return
     */
	public static Date getPrimeiroDiaMes(Date date){

		Calendar first = Calendar.getInstance();
		first.setTime(DateUtils.toSqlDate(date));
		first.set(Calendar.DAY_OF_MONTH, 1);
		return new java.sql.Date(first.getTimeInMillis());
	}

	/**
	 * Retorna um date com o ultimo dia do mes inserido
	 * @param date
	 * @return
     */
	public static Date getUltimoDiaMes(Date date){

		Calendar last = Calendar.getInstance();
		last.setTime(DateUtils.toSqlDate(date));
		last.set(Calendar.DAY_OF_MONTH, 1);
		last.add(Calendar.MONTH, 1);
		last.add(Calendar.DAY_OF_MONTH, -1);

		return new java.sql.Date(last.getTimeInMillis());
	}
}
