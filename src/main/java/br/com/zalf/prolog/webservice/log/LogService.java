package br.com.zalf.prolog.webservice.log;

import java.sql.SQLException;

public class LogService {

	private LogDao dao = new LogDaoImpl();

	public boolean insert(String log, String indicador) {
		try {
			return dao.insert(log, indicador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
