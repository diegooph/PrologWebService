package br.com.zalf.prolog.webservice.frota.checklistModelo;

import br.com.zalf.prolog.frota.checklist.ModeloChecklist;
import br.com.zalf.prolog.frota.checklist.PerguntaRespostaChecklist;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe ChecklistModeloService responsavel por comunicar-se com a interface DAO
 */
public class ChecklistModeloService {

	private ChecklistModeloDao dao = new ChecklistModeloDaoImpl();
	
	public List<ModeloChecklist> getModelosChecklistByCodUnidadeByCodFuncao(Long codUnidade, String codFuncao) {
		try{
			return dao.getModelosChecklistByCodUnidadeByCodFuncao(codUnidade, codFuncao);
		}catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public ModeloChecklist getModeloChecklist(Long codModelo, Long codUnidade){
		try{
			return dao.getModeloChecklist(codModelo, codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean setModeloChecklistInativo (Long codUnidade, Long codModelo) {
		try{
			return dao.setModeloChecklistInativo(codUnidade, codModelo);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean insertModeloChecklist(ModeloChecklist modeloChecklist) {
		try{
			return dao.insertModeloChecklist(modeloChecklist);
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public List<PerguntaRespostaChecklist> getPerguntas(Long codUnidade, Long codModelo){
		try{
			return dao.getPerguntas(codUnidade, codModelo);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}
