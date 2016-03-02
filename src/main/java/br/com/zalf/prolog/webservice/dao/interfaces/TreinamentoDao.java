package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.treinamento.Treinamento;
import br.com.zalf.prolog.models.treinamento.TreinamentoColaborador;
/**
 * Contém os métodos para manipular os treinamentos
 */
public interface TreinamentoDao {
	/**
	 * Busca os treinamentos ainda não visualizados por um colaborador específico
	 * @param cpf um cpf, serão buscados os treinamentos ainda não visualizados por ele
	 * @param token para verificar se o solicitante esta devidamente logado
	 * @return lista de Treinamento
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Treinamento> getNaoVistosColaborador(Long cpf, String token) throws SQLException;
	/**
	 * Busca os treinamentos já visualizados por um colaborador específico
	 * @param cpf um cpf, serão buscados os treinamentos já visualizados por ele
	 * @param token para verificar se o solicitante esta devidamente logado
	 * @return lista de Treinamento
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Treinamento> getVistosColaborador(Long cpf, String token) throws SQLException;
	/**
	 * Insere uma linha na tabela treinamento_colaborador, na qual armazena a data
	 *  que um treinamento foi visualizado, associando o código do treianmento com um cpf
	 * @param treinamentoColaborador contém o código do treinamento e cpf do colaborador que visualizou
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível realizar o insert
	 */
	boolean marcarTreinamentoComoVisto(TreinamentoColaborador treinamentoColaborador) throws SQLException;
}
