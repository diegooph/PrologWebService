package br.com.zalf.prolog.webservice.log;

import java.sql.SQLException;

/**
 * Created by didi on 9/15/16.
 */
public interface LogDao {

	boolean insert(String log, String indicador) throws SQLException;
}
