package br.com.zalf.prolog.webservice.imports.mapa;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Contém os métodos para import da tabela Mapa (ambev: 2art)
 */
public interface MapaDao {

	boolean insertOrUpdateMapa (String path, Long codUnidade)throws SQLException, IOException, ParseException;
	
}
