package br.com.zalf.prolog.webservice.entrega.metas;

import java.sql.SQLException;

/**
 * Contém os métodos para gerenciamento das metas operacionais (indicadores)
 */
public interface MetasDao {

	Metas getByCodUnidade(Long codUnidade) throws SQLException;

	boolean update(Metas metas, Long codUnidade) throws SQLException;

}