package br.com.zalf.prolog.webservice.entrega;

import br.com.zalf.prolog.webservice.commons.util.Log;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jean on 18/01/16.
 */
public class ImportUtils {

	/**
	 * Converte uma string contendo data e hora para um timestamp
	 * @param data
	 * @return
     */
	public static Date toTimestamp(String data){
		DateFormat dateFormat;
		Date date = null;
		Log.d("data", data);
		try {
			if(data.trim().replace(" ", "").length() == 13){
				dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
                date = dateFormat.parse(data);
			}else if(data.trim().length() == 10){
				dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                date = dateFormat.parse(data);
			}else if(data.trim().replace(" ", "").length() == 15){
				dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                date = dateFormat.parse(data);
			}
			else {
                return null;
			}
			date = dateFormat.parse(data);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Converte uma String para um Time
	 * @param hora uma String contendo uma hora
	 * @return um Time
	 */
	public static Time toTime(String hora) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm");
		hora = hora.replace(" ","");
		// verifica quando tem 2 espaços extras na hora (tabela mapa)
		if(hora.length() == 6){
			hora = hora.substring(1,6);
			dateFormat = new SimpleDateFormat("HH:mm");
		}
		if(hora.length() == 4 || hora.length() == 5 || hora.length() == 8){
			dateFormat = new SimpleDateFormat("HH:mm");
		}
		Time time = null;
		try {
			Date date = dateFormat.parse(hora);
			time = new Time(date.getTime());
		}finally {
		}
		return time;
	}

}
