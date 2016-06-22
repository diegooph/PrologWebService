package br.com.zalf.prolog.webservice.frota.checklistModelo;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.checklist.ModeloChecklist;

/**
 * Contém os métodos para manipular os checklists no banco de dados 
 */
public interface ChecklistModeloDao {
	
	public List<ModeloChecklist> getModelosChecklistByCodUnidadeByCodFuncao(Long codUnidade, String codFuncao) throws SQLException;
	
	public List<ModeloChecklist> getModeloChecklist(Long codModelo, Long codUnidade) throws SQLException;
	
	public boolean insertModeloChecklist(ModeloChecklist modeloChecklist) throws SQLException;
	
	public boolean setModeloChecklistInativo (Long codUnidade, Long codModelo) throws SQLException;
	
}
