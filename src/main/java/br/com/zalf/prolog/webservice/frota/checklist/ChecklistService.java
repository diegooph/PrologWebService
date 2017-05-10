package br.com.zalf.prolog.webservice.frota.checklist;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Classe ChecklistService responsavel por comunicar-se com a interface DAO
 */
public class ChecklistService {

	private ChecklistDao dao = new ChecklistDaoImpl();

	public List<String> getUrlImagensPerguntas(Long codUnidade, Long codFuncao){
		try{
			return dao.getUrlImagensPerguntas(codUnidade, codFuncao);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean insert(Checklist checklist) {
		try {
			return dao.insert(checklist);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(Long codUnidade, Long codFuncao){
		try{
			return dao.getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public NovoChecklistHolder getNovoChecklistHolder(Long codUnidade, Long codModelo, String placa){
		try{
			return dao.getNovoChecklistHolder(codUnidade, codModelo, placa);
		}catch(SQLException e){
			e.printStackTrace();
			return new NovoChecklistHolder();
		}
	}
	
	public Checklist getByCod(Long codigo) {
		try {
			return dao.getByCod(codigo);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Checklist> getAll(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			Long codUnidade, String placa, long limit, long offset) {
		try {
			return dao.getAll(dataInicial, dataFinal, equipe, codUnidade, placa, limit, offset);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Checklist>();
		}
	}
	
	public List<Checklist> getByColaborador(Long cpf, int limit, long offset) {
		try {
			return dao.getByColaborador(cpf, limit, offset);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Checklist>();
		}
	}

	public List<VeiculoLiberacao> getStatusLiberacaoVeiculos(Long codUnidade){
		try{
			return dao.getStatusLiberacaoVeiculos(codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

}
