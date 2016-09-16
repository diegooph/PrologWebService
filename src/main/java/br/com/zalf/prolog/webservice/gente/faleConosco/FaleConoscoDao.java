package br.com.zalf.prolog.webservice.gente.faleConosco;

import br.com.zalf.prolog.commons.network.Request;
import br.com.zalf.prolog.gente.fale_conosco.FaleConosco;

import java.sql.SQLException;
import java.util.List;


/**
 * Contém os métodos para manipular os fale conosco
 */
public interface FaleConoscoDao {

	/**
	 * insere uma nova requisição faleConosco
	 * @param faleConosco objeto faleConosco
	 * @param codUnidade código da unidade
	 * @return valor da operação
	 * @throws SQLException caso operação falhar
	 */
	boolean insert(FaleConosco faleConosco, Long codUnidade) throws SQLException;

	/**
	 * Atualiza/Edita um FaleConosco existente no banco de dados
	 * @param faleConosco contém o FaleConosco
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível atualizar/editar
	 */
	boolean update(FaleConosco faleConosco) throws SQLException;

	/**
	 * Deleta um FaleConsco do banco de dados
	 * @param request contendo os dados do objeto a ser deletado
	 * @return resultado da requisição
	 * @throws SQLException caso não seja possível deletar
	 */
	boolean delete(Request<FaleConosco> request) throws SQLException;

	/**
	 * Busca um FaleConosco pelo código
	 * @param codigo código do faleConosco
	 * @param codUnidade código da unidade
	 * @return um FaleConosco
	 * @throws SQLException caso não seja possível buscar 
	 */
	FaleConosco getByCod(Long codigo, Long codUnidade) throws Exception;

	/**
	 * Busca os fale conosco entre as datas de entrada
	 * @param dataInicial data inicial
	 * @param dataFinal data final
	 * @param limit limite de busca no banco
	 * @param offset offset de busca no banco
	 * @param equipe equipe
	 * @param codUnidade código da unidade
	 * @param status status
	 * @param categoria categoria do faleConosco
	 * @return uma lista de FaleConosco
	 * @throws Exception caso não seja possivel buscar
	 */
	List<FaleConosco> getAll(long dataInicial, long dataFinal, int limit, int offset,
							 String equipe, Long codUnidade, String status, String categoria) throws Exception;

	/**
	 * Busca os FaleConosco de um determinado colaborador
	 * @param cpf do colaborador a ser buscado os FaleConosco
	 * @param status status do FaleConosco
	 * @return lista de FaleConosco
	 * @throws SQLException caso não seja possivel buscar
	 */
	List<FaleConosco> getByColaborador(Long cpf, String status) throws Exception;

	/**
	 * insere um feedback no faleConosco
	 * @param faleConosco objeto FaleConosco
	 * @param codUnidade código da unidade
	 * @return valor da operação
	 * @throws SQLException caso algo der errado
	 */
	boolean insertFeedback(FaleConosco faleConosco, Long codUnidade) throws SQLException;
}
