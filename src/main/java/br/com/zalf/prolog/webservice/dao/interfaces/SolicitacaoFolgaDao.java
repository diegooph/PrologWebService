package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.SolicitacaoFolga;
/**
 * Contém os métodos para manipular as solicitações de folga
 */
public interface SolicitacaoFolgaDao {
	/**
	 * Insere uma SolicitacaoFolga no banco de dados
	 * @param solicitadao uma SolicitacaoFolga
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível inserir a solicitação de folga
	 */
	boolean insert(SolicitacaoFolga solicitacao) throws SQLException;
	/**
	 * Atualiza/Edita uma solicitação de folga
	 * @param request contendo a solicitação de folga a ser atualizada/editada, 
	 * além dos dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível atualizar
	 */
	boolean update(Request<SolicitacaoFolga> request) throws SQLException;
	/**
	 * Delta uma solicitação de folga do banco de dados
	 * @param request contendo a solicitação a ser deletada, além dos dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível deletar
	 */
	boolean delete(Request<SolicitacaoFolga> request) throws SQLException;
	/**
	 * Busca uma SolicitacaoFolga pelo seu código
	 * @param request contendo a solicitação a ser buscada e os dados do solicitante
	 * @return uma SolicitacaoFolga
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	SolicitacaoFolga getByCod(Request<?> request) throws SQLException;
	/**
	 * Busca todas as solicitações de folga
	 * @param request contendo os dados do solicitante
	 * @return lista de SolicitacaoFolga
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<SolicitacaoFolga> getAll(Request<?> request) throws SQLException;
	/**
	 * Busca as solicitações de folga de determinado colaborador
	 * @param cpf um cpf, ao qual serão buscados suas solicitações de folga
	 * @param token para verificar se o solicitante esta devidamente logado
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível fazer a busca
	 */
	List<SolicitacaoFolga> getByColaborador(Long cpf, String token) throws SQLException;
}
