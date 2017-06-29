package br.com.zalf.prolog.webservice.log;

import java.sql.SQLException;

/**
 * Created by didi on 9/15/16.
 */
public interface LogDao {

	/**
	 *
	 * @param log descrição do log
	 * @param identificador identificador do log
	 * @return valor da operação
	 * @throws SQLException caso ocorrer erro no banco
	 */
	boolean insert(String log, String identificador) throws SQLException;
}
