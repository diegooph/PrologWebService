package br.com.zalf.prolog.webservice.imports.tracking;

import br.com.zalf.prolog.webservice.commons.colaborador.Colaborador;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by didi on 9/15/16.
 */
public interface TrackingDao {

	/**
	 * Insere um mapa caso não exista, atualiza caso exista
	 * @param colaborador um Colaborador, que esta fazendo o upload
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível realizar o import
	 */
	public boolean insertOrUpdateTracking (String path, Colaborador colaborador)throws SQLException, IOException;

}
