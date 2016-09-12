package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import br.com.zalf.prolog.commons.network.AbstractResponse;
import br.com.zalf.prolog.commons.network.Request;
import br.com.zalf.prolog.gente.solicitacao_folga.SolicitacaoFolga;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Contém os métodos para manipular as solicitações de folga
 */
public interface SolicitacaoFolgaDao {
	/**
	 * Insere uma SolicitacaoFolga no banco de dados
	 * @param solicitadao uma SolicitacaoFolga
	 * @return Reponse.Ok com o codigo gerado, ou .Error caso não seja possível inserir
	 * @throws SQLException caso não seja possível inserir a solicitação de folga
	 */
	AbstractResponse insert(SolicitacaoFolga solicitacao) throws SQLException;
	/**
	 * Atualiza/Edita uma solicitação de folga
	 * @param request contendo a solicitação de folga a ser atualizada/editada, 
	 * além dos dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível atualizar
	 */
	boolean update(SolicitacaoFolga solicitacaoFolga) throws SQLException;
	/**
	 * Delta uma solicitação de folga do banco de dados
	 * @param id da solicitação de folga a ser deletada
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível deletar
	 */
	boolean delete(Long codigo) throws SQLException;
	/**
	 * Busca uma SolicitacaoFolga pelo seu código
	 * @param request contendo a solicitação a ser buscada e os dados do solicitante
	 * @return uma SolicitacaoFolga
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	SolicitacaoFolga getByCod(Request<?> request) throws SQLException;
	/**
	 * Busca todas as solicitações, respeitando os filtros
	 * @param dataInicial um Date
	 * @param dataFinal um Date
	 * @param codUnidade código da unidade a serem buscadas as solicitações
	 * @param codEquipe código da equipe a serem buscadas as solicitações
	 * @param status status a ser buscado
	 * @param cpfColaborador usado para fazer uma busca de um colaborador especifico, se for null busca todos
	 * @return ums lista de SolicitacaoFolga
	 * @throws SQLException caso não seja possivel realizara a busca
	 */
	List<SolicitacaoFolga> getAll(LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String codEquipe, String status, Long cpfColaborador) throws SQLException;
	/**
	 * Busca as solicitações de folga de determinado colaborador
	 * @param cpf um cpf, ao qual serão buscados suas solicitações de folga
	 * @param token para verificar se o solicitante esta devidamente logado
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível fazer a busca
	 */
	List<SolicitacaoFolga> getByColaborador(Long cpf, String token) throws SQLException;
}
