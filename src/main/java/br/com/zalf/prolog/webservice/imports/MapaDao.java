package br.com.zalf.prolog.webservice.imports;

import br.com.zalf.prolog.commons.colaborador.Colaborador;
import br.com.zalf.prolog.commons.imports.MapaImport;

import java.sql.SQLException;
import java.util.List;

/**
 * Contém os métodos para import da tabela Mapa (ambev: 2art)
 */
public interface MapaDao {
	/**
	 * Insere um mapa caso não exista, atualiza caso exista
	 * @param listMapas lista de MapaImport
	 * @param colaborador um Colaborador, que esta fazendo o upload
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível realizar o import
	 */
	public boolean insertOrUpdateMapa (List<MapaImport> listMapas, Colaborador colaborador) throws SQLException;
	
}
