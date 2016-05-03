package br.com.zalf.prolog.webservice.metas;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Metas;
import br.com.zalf.prolog.models.Request;
/**
 * Contém os métodos para gerenciamento das metas operacionais (indicadores)
 */
public interface MetasDao {
	/**
	 * Busca metas de acordo com a unidade informada
	 * @param codUnidade código da unidade a ser buscadas as metas
	 * @param cpf um cpf
	 * @param token para verificar se o usuário esta devidamente logado
	 * @return lista de Metas (double ou time)
	 * @throws SQLException caso não seja possível buscar
	 */
	public List<Metas<?>> getByCodUnidade(Long codUnidade, Long cpf, String token) throws SQLException;
	/**
	 * Atualiza o valor de determinada meta
	 * @param request contém a meta atualizada
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possivel realizar o update
	 */
	public boolean updateByCod(Request<Metas> request) throws SQLException;

}
