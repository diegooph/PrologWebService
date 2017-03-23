package br.com.zalf.prolog.webservice.metas;

import br.com.zalf.prolog.entrega.indicador.Meta;

import java.sql.SQLException;

/**
 * Contém os métodos para gerenciamento das metas operacionais (indicadores)
 */
public interface MetasDao {

	Meta getByCodUnidade(Long codUnidade) throws SQLException;

	boolean update(Meta meta, Long codUnidade) throws SQLException;

}
