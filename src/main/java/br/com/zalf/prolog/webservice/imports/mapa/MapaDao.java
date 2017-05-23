package br.com.zalf.prolog.webservice.imports.mapa;

import br.com.zalf.prolog.webservice.colaborador.Colaborador;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Contém os métodos para import da tabela Mapa (ambev: 2art)
 */
public interface MapaDao {

	boolean insertOrUpdateMapa (String path, Colaborador colaborador)throws SQLException, IOException, ParseException;
	
}
