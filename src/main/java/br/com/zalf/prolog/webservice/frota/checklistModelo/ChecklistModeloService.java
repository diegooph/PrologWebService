package br.com.zalf.prolog.webservice.frota.checklistModelo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.checklist.ModeloChecklist;
import br.com.zalf.prolog.models.checklist.PerguntaRespostaChecklist;

public class ChecklistModeloService {
	private ChecklistModeloDaoImpl dao = new ChecklistModeloDaoImpl();
	
	public List<ModeloChecklist> getModelosChecklistByCodUnidadeByCodFuncao(Long codUnidade, String codFuncao) {
		try{
			return dao.getModelosChecklistByCodUnidadeByCodFuncao(codUnidade, codFuncao);
		}catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public List<ModeloChecklist> getModeloChecklist(Long codModelo, Long codUnidade){
		try{
			return dao.getModeloChecklist(codModelo, codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
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
	
	public List<PerguntaRespostaChecklist> getPerguntas(Long codUnidade, Long codFuncao){
		try{
			return dao.getPerguntas(codUnidade, codFuncao);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}
