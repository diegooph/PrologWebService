package br.com.zalf.prolog.webservice.veiculo;

import java.sql.SQLException;
import java.util.List;

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
	boolean insert(Veiculo veiculo, Long codUnidade) throws SQLException;
	/**
	 * Atualiza os dados de um veículo
	 * @param request contém os dados do veículo e os dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível realizar o update
	 */
	boolean update(Veiculo veiculo, String placaOriginal) throws SQLException;
	
	/**
	 * Seta o veiculo como inativo no banco de dados
	 * @param placa
	 * @return
	 * @throws SQLException
	 */
	public boolean delete(String placa) throws SQLException;
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
