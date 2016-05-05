package br.com.zalf.prolog.webservice.veiculo;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.Veiculo;
/**
 * Contém os métodos para manipular os veículos
 */
public interface VeiculoDao {
	/**
	 * Insere um novo veículo 
	 * @param request contém os dados do veículo a ser inserido, além dos dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível realizar o insert
	 */
	boolean insert(Request<Veiculo> request) throws SQLException;
	/**
	 * Atualiza os dados de um veículo
	 * @param request contém os dados do veículo e os dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível realizar o update
	 */
	boolean update(String placa, String placaEditada, String modelo, boolean isAtivo) throws SQLException;
	/**
	 * Busca todos os veiculos cadastrados em uma determinada unidade
	 * @param request contém os dados do solicitante e código da unidade onde serõa buscados os veículos
	 * @return lista de Veiculo
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Veiculo> getAll(Request<?> request) throws SQLException;
	/**
	 * Busca os veículos ativos de uma determinada unidade
	 * @param codUnidade um código
	 * @return lista de Veiculo
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Veiculo> getVeiculosAtivosByUnidade(Long codUnidade) throws SQLException;
	/**
	 * Busca os veículos ativos de uma determinada unidade
	 * @param cpf um cpf, ao qual será feita a busca da unidade
	 * @param token para verificar se o solicitante esta devidamente logado
	 * @return lista de Veiculo
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(Long cpf) 
			throws SQLException;
}
