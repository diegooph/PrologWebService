package br.com.zalf.prolog.webservice.entrega.metas;

import java.sql.SQLException;

/**
 * Contém os métodos para gerenciamento das metas operacionais (indicadores)
 */
public interface MetasDao {

	/**
	 * Retorna as metas da unidade.
	 *
	 * @param codUnidade código da unidade
	 * @return um objeto {@link Metas} contendo os valores de cada meta daquela unidade
	 * @throws SQLException caso a operação não pôde ser concluída
	 */
	Metas getByCodUnidade(Long codUnidade) throws SQLException;

	/**
	 * Altera as metas de uma unidade.
	 *
	 * @param metas      objeto contendo as novas metas
	 * @param codUnidade código da unidade que irá atualizar as metas
	 * @return verdadeiro caso operação realizada com sucesso, falso caso contrário
	 * @throws SQLException caso a operação não pôde ser concluída
	 */
	boolean update(Metas metas, Long codUnidade) throws SQLException;

}