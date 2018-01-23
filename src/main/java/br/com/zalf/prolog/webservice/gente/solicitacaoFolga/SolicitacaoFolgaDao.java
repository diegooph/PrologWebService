package br.com.zalf.prolog.webservice.gente.solicitacaoFolga;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Contém os métodos para manipular as solicitações de folga
 */
public interface SolicitacaoFolgaDao {

	/**
	 * Insere uma SolicitacaoFolga no banco de dados
	 * @param solicitacao uma SolicitacaoFolga
	 * @return Reponse.ok com o codigo gerado, ou .error caso não seja possível inserir
	 * @throws SQLException caso não seja possível inserir a solicitação de folga
	 */
	AbstractResponse insert(SolicitacaoFolga solicitacao) throws SQLException;

	/**
	 * Atualiza/Edita uma solicitação de folga
	 * @param solicitacaoFolga solicitação de folga a ser atualizada/editada,
	 * além dos dados do solicitante
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível atualizar
	 */
	boolean update(SolicitacaoFolga solicitacaoFolga) throws SQLException;

	/**
	 * Delta uma solicitação de folga do banco de dados
	 * @param codigo da solicitação de folga a ser deletada
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível deletar
	 */
	boolean delete(Long codigo) throws SQLException;

	/**
	 * Busca todas as solicitações, respeitando os filtros
	 * @param dataInicial um Date
	 * @param dataFinal um Date
	 * @param codUnidade código da unidade a serem buscadas as solicitações
	 * @param codEquipe código da equipe a serem buscadas as solicitações
	 * @param status status a ser buscado
	 * @param cpfColaborador usado para fazer uma busca de um colaborador especifico, se for % busca todos
	 * @return ums lista de SolicitacaoFolga
	 * @throws SQLException caso não seja possivel realizara a busca
	 */
	List<SolicitacaoFolga> getAll(LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String codEquipe,
								  String status, String cpfColaborador) throws SQLException;

	/**
	 * Busca as solicitações de folga de determinado colaborador
	 * @param cpf um cpf, ao qual serão buscados suas solicitações de folga
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível fazer a busca
	 */
	List<SolicitacaoFolga> getByColaborador(Long cpf) throws SQLException;
}
