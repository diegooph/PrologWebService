package br.com.zalf.prolog.webservice.frota.checklist;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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
	 * @param checklist checklist a ser atualizado
	 * @return boolean com o resultado da operação
	 * @throws SQLException caso não seja possível atualizar o checklist
	 */
	boolean update(Checklist checklist) throws SQLException;
	/**
	 * Deleta um checklist do banco de dados
	 * @param codChecklist codigo do checklist a ser deletado
	 * @return boolean com o resultado da operção
	 * @throws SQLException caso não seja possível deletar o checklist
	 */
	boolean delete(long codChecklist) throws SQLException;
	/**
	 * Busca um checklist pelo seu código único
	 * @param codChecklist codigo do checklist a ser buscado
	 * @return um checklist
	 * @throws SQLException caso não consiga buscar o checklist no banco de dados
	 */
	Checklist getByCod(long codChecklist) throws SQLException;

	/**
	 * Busca todos os checklists, respeitando os filtros aplicados (recebidos por parâmetro)
	 * @param dataInicial uma data
	 * @param dataFinal uma data
	 * @param equipe string contendo o nome da equipe ou '%' para o caso de buscar os checklists de todas
	 * @param codUnidade código da unidade
	 * @param limit quantidade de checks buscados no banco
	 * @param offset a partir de qual check será  abusca
	 * @return lista de Checklist
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	List<Checklist> getAll(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade, String placa, long limit, long offset) throws SQLException;
	
	/**
	 * Busca os checklists realizados por um colaborador
	 * @param cpf um cpf
	 * @param limit quantidade de checks buscados no banco
	 * @param offset a partir de qual check será  abusca
	 * @return lista de Checklist
	 * @throws SQLException caso não seja possível realizar a busca no banco de dados
	 */
	List<Checklist> getByColaborador(Long cpf, int limit, long offset) throws SQLException;
	
	
}
