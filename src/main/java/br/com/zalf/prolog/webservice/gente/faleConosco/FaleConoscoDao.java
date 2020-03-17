package br.com.zalf.prolog.webservice.gente.faleConosco;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;

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
	 * @return código do fale conosco inserido
	 * @throws SQLException caso operação falhar
	 */
	Long insert(FaleConosco faleConosco, Long codUnidade) throws SQLException;

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
	 * @param cpf CPF do {@link Colaborador} que enviou o {@link FaleConosco} ou '%' para buscar de todos os Colaboradores
	 * @param equipe equipe
	 * @param codUnidade código da unidade
	 * @param status status
	 * @param categoria categoria do faleConosco
	 * @return uma lista de FaleConosco
	 * @throws Exception caso não seja possivel buscar
	 */
	List<FaleConosco> getAll(long dataInicial, long dataFinal, int limit, int offset, String cpf,
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