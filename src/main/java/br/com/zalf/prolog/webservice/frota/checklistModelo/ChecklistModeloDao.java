package br.com.zalf.prolog.webservice.frota.checklistModelo;


import br.com.zalf.prolog.frota.checklist.ModeloChecklist;
import br.com.zalf.prolog.frota.checklist.PerguntaRespostaChecklist;

import java.sql.SQLException;
import java.util.List;

/**
 * Contém os métodos para manipular os checklists no banco de dados 
 */
public interface ChecklistModeloDao {

	/**
	 * busca as perguntas do checklist
	 * @param codUnidade código da unidade
	 * @param codModelo código do modelo
	 * @return lista de perguntas do checklist
	 * @throws SQLException se ocorrer erro na execução
	 */
	List<PerguntaRespostaChecklist> getPerguntas(Long codUnidade, Long codModelo) throws SQLException;

	/**
	 * busca o modelo de checklist usando código da unidade e função
	 * @param codUnidade código da unidade
	 * @param codFuncao código da função
	 * @return lista de modelo do checklist
	 * @throws SQLException se ocorrer erro no banco
	 */
	List<ModeloChecklist> getModelosChecklistByCodUnidadeByCodFuncao(Long codUnidade, String codFuncao) throws SQLException;

	/**
	 * busca um modelo de checklist atraves do modelo e da unidade
	 * @param codModelo código do modelo
	 * @param codUnidade código da unidade
	 * @return um {@link ModeloChecklist}
	 * @throws SQLException se ocorrer erro no bando
	 */
	ModeloChecklist getModeloChecklist(Long codModelo, Long codUnidade) throws SQLException;

	/**
	 * insere um checklist
	 * @param modeloChecklist o checklist
	 * @return valor da operação
	 * @throws SQLException caso ocorrer erro no banco
	 */
	boolean insertModeloChecklist(ModeloChecklist modeloChecklist) throws SQLException;

	/**
	 * marca como inativo o checklist através das informações de unidade e modelo
	 * @param codUnidade código da unidade
	 * @param codModelo código do modelo
	 * @return valor da operação
	 * @throws SQLException caso ocorrer erro no banco
	 */
	boolean setModeloChecklistInativo (Long codUnidade, Long codModelo) throws SQLException;
	
}
