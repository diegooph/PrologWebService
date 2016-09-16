package br.com.zalf.prolog.webservice.seguranca.gsd;

import br.com.zalf.prolog.commons.network.Request;
import br.com.zalf.prolog.commons.questoes.Pergunta;
import br.com.zalf.prolog.seguranca.gsd.Gsd;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Contém os métodos para manipular os formulários GSD 
 */
public interface GsdDao {

	/**
	 * Insere um GSD no banco de dados
	 * @param gsd um GSD
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível inserir no banco
	 */
	boolean insert(Gsd gsd) throws SQLException;

	/**
	 * Atualiza/Edita um GSD do banco de dados
	 * @param request contém o GSD a ser atualizado e dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível realizar o update
	 */
	boolean update(Request<Gsd> request) throws SQLException;

	/**
	 * Deleta um GSD do banco de dados
	 * @param request contém o GSD a ser deletado e dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível deletar o GSD
	 */
	boolean delete(Request<Gsd> request) throws SQLException;

	/**
	 * Busca um GSD pelo seu código
	 * @param request contém o GSD a ser buscado e dados do solicitante
	 * @return um GSD
	 * @throws SQLException caso não seja possível buscar
	 */
	Gsd getByCod(Request<?> request) throws SQLException;

	/**
	 * Busca todos os GSD respeitando os filtros aplicados
	 * @param dataInicial 
	 * @param dataFinal
	 * @param equipe
	 * @param codUnidade
	 * @param limit
	 * @param offset
	 * @return
	 * @throws SQLException
	 */
	List<Gsd> getAll(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade, long limit, long offset) throws SQLException;

	/**
	 * Busca todos os GSD de um determinado colaborador
	 * @param cpf do colaborador a buscar os GSD
	 * @param token para verificar se esta devidamente logado
	 * @return lista de GSD
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Gsd> getByColaborador(Long cpf, String token) throws SQLException;

	/**
	 * Busca todos os GSD de um determinado avaliador
	 * @param cpf do avaliador a serem buscados os GSD
	 * @param token para verificar se esta devidamente logado
	 * @return lista de GSD
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Gsd> getByAvaliador(Long cpf, String token) throws SQLException;

	/**
	 * Busca as perguntas do formulário GSD
	 * @return lista de Pergunta
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Pergunta> getPerguntas() throws SQLException;

	/**
	 * Busca de todos os GSD feitos por outros avaliadores
	 * @param cpf para realizar a busca
	 * @param token para verificar se esta devidamente logado
	 * @return lista de GSD
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Gsd> getAllExcetoAvaliador(Long cpf, String token) throws SQLException;
}
