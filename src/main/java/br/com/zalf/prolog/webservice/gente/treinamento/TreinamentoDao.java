package br.com.zalf.prolog.webservice.gente.treinamento;

import br.com.zalf.prolog.gente.treinamento.Treinamento;
import br.com.zalf.prolog.gente.treinamento.TreinamentoColaborador;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Contém os métodos para manipular os treinamentos
 */
public interface TreinamentoDao {

	/**
	 * busca todos os trienamentos dentre as datas inicial e final
	 * @param dataInicial data inicial
	 * @param dataFinal data final
	 * @param codFuncao código da função
	 * @param codUnidade código da unidade
	 * @param limit limit de busca no banco
	 * @param offset offset de busca no banco
	 * @return uma lista de treinamentos
	 * @throws SQLException caso operação falhar
	 */
	List<Treinamento> getAll (LocalDate dataInicial, LocalDate dataFinal, String codFuncao,
							  Long codUnidade, long limit, long offset) throws SQLException;

	/**
	 * Busca os treinamentos ainda não visualizados por um colaborador específico
	 * @param cpf um cpf, serão buscados os treinamentos ainda não visualizados por ele
	 * @return lista de Treinamento
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Treinamento> getNaoVistosColaborador(Long cpf) throws SQLException;

	/**
	 * Busca os treinamentos já visualizados por um colaborador específico
	 * @param cpf um cpf, serão buscados os treinamentos já visualizados por ele
	 * @return lista de Treinamento
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Treinamento> getVistosColaborador(Long cpf) throws SQLException;

	/**
	 * Insere uma linha na tabela treinamento_colaborador, na qual armazena a data
	 *  que um treinamento foi visualizado, associando o código do treianmento com um cpf
	 * @param treinamentoColaborador contém o código do treinamento e cpf do colaborador que visualizou
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível realizar o insert
	 */
	boolean marcarTreinamentoComoVisto(TreinamentoColaborador treinamentoColaborador) throws SQLException;

	/**
	 * inserir um treinamento
	 * @param treinamento trienamento a ser inserido
	 * @return valor da operação
	 * @throws SQLException caso operação falhar
	 */
	boolean insert(Treinamento treinamento) throws SQLException;

	/**
	 * busca os colaboradores que visualizaram o treinamento
	 * @param codTreinamento código do treinamento
	 * @param codUnidade código da unidade
	 * @return uma lista de colaboradores
	 * @throws SQLException caso operação falhar
	 */
	List<TreinamentoColaborador> getVisualizacoesByTreinamento(Long codTreinamento, Long codUnidade) throws SQLException;

	/**
	 * Busca um treinamento a partir do seu código
	 * @param codTreinamento código do treinamento
	 * @param codUnidade código da unidade
	 * @return um Treinamento
	 * @throws SQLException caso não seja possível realizar a busca
     */
	public Treinamento getTreinamentoByCod(Long codTreinamento, Long codUnidade) throws SQLException;
}
