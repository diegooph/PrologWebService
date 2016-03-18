package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import br.com.zalf.prolog.models.Pergunta;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.checklist.Checklist;
/**
 * Contém os métodos para manipular os checklists no banco de dados 
 */
public interface ChecklistDao {
	/**
	 * Insere um checklist no banco de dados
	 * @param checklist um checklist
	 * @return boolean com o resultado da operação
	 * @throws SQLException caso não seja possível inserir o checklist no banco de dados
	 */
	boolean insert(Checklist checklist) throws SQLException;
	/**
	 * Atualiza um checklist no banco de dados
	 * @param request objeto contendo o checklist a ser atualizado e dados do 
	 * usuário solicitante
	 * @return boolean com o resultado da operação
	 * @throws SQLException caso não seja possível atualizar o checklist
	 */
	boolean update(Request<Checklist> request) throws SQLException;
	/**
	 * Deleta um checklist do banco de dados
	 * @param request contém o checklist a ser deletado e dados do solicitante
	 * @return boolean com o resultado da operção
	 * @throws SQLException caso não seja possível deletar o checklist
	 */
	boolean delete(Request<Checklist> request) throws SQLException;
	/**
	 * Busca um checklist pelo seu código único
	 * @param Request contento os dados do checklist a ser buscado e dados do solicitante
	 * @return um checklist
	 * @throws SQLException caso não consiga buscar o checklist no banco de dados
	 */
	Checklist getByCod(Request<?> request) throws SQLException;
	/**
	 * Busca todos os checklists do banco de dados
	 * @param request contendo os dados do solicitante
	 * @return lista de checklist
	 * @throws SQLException caso não seja possível realizar a busca no banco de dados
	 */
	List<Checklist> getAll(Request<?> request) throws SQLException;
	/**
	 * Busca todos os checks de uma unidade respeitando o período selecionado
	 * @param request contém os dados do solicitante, token e cod da unidade a ser buscada
	 * @param dataInicial uma data
	 * @param dataFinal uma data
	 * @param limit um limit
	 * @param offset um offset
	 * @return uma lista de Checklist
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	public List<Checklist> getAllByCodUnidade(Long cpf, String token, Long codUnidade, LocalDate dataInicial, LocalDate dataFinal, int limit, long offset) throws SQLException;
	/**
	 * Busca os checklists realizados por um colaborador
	 * @param cpf a ser consultado
	 * @param token para verificar se o usuário solicitante esta logado
	 * @param offset para implementação do load more
	 * @return lista de checklist
	 * @throws SQLException caso não seja possível realizar a busca no banco de dados
	 */
	List<Checklist> getByColaborador(Long cpf, String token, long offset) throws SQLException;
	/**
	 * Busca das perguntas do checklist
	 * @return lista de Pergunta
	 * @throws SQLException caso não seja possível realizar a busca no banco de dados
	 */
	List<Pergunta> getPerguntas() throws SQLException;
	/**
	 * Busca os checklists realizados por outros colaboradores
	 * @param cpf do solicitante, no qual não aparecerão seus checks na busca
	 * @param offset para implementação do load more
	 * @return lista de checklist
	 * @throws SQLException caso não seja possível realizar a busca no banco de dados
	 */
	List<Checklist> getAllExcetoColaborador(Long cpf, long offset) throws SQLException;
}
