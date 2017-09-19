package br.com.zalf.prolog.webservice.imports.tracking;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Created by didi on 9/15/16.
 */
public interface TrackingDao {

	/**
	 * Insere um mapa caso não exista, atualiza caso exista
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível realizar o import
	 */
	public boolean insertOrUpdateTracking (String path, Long codUnidade)throws SQLException, IOException, ParseException;

}
