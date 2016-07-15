package br.com.zalf.prolog.webservice.log;

import java.sql.SQLException;

public class LogService {

	LogDaoImpl dao = new LogDaoImpl();

	public boolean insert(String log) {
		try {
			return dao.insert(log);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
