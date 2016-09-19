package br.com.zalf.prolog.webservice.imports;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.imports.TrackingImport;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by didi on 9/15/16.
 */
public interface TrackingDao {

	/**
	 * Insere um mapa caso não exista, atualiza caso exista
	 * @param listTracking lista de MapaImport
	 * @param colaborador um Colaborador, que esta fazendo o upload
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível realizar o import
	 */
	boolean insertOrUpdateTracking(List<TrackingImport> listTracking, Colaborador colaborador) throws SQLException;

}
