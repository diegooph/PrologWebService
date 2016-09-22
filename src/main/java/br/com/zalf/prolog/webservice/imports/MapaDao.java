package br.com.zalf.prolog.webservice.imports;

import br.com.zalf.prolog.commons.colaborador.Colaborador;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Contém os métodos para import da tabela Mapa (ambev: 2art)
 */
public interface MapaDao {

	boolean insertOrUpdateMapa (String path, Colaborador colaborador)throws SQLException, IOException;
	
}
