package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.checklist.Checklist;
import br.com.zalf.prolog.models.checklist.NovoChecklistHolder;
import br.com.zalf.prolog.models.checklist.PerguntaRespostaChecklist;
import br.com.zalf.prolog.models.checklist.VeiculoLiberacao;
import br.com.zalf.prolog.webservice.dao.ChecklistDaoImpl;

public class ChecklistService {
	private ChecklistDaoImpl dao = new ChecklistDaoImpl();
	
	public List<PerguntaRespostaChecklist> getPerguntas(Long codUnidade){
		try{
			return dao.getPerguntas(codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return new ArrayList<>();
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
	
	public NovoChecklistHolder getNovoChecklistHolder(Long codUnidade){
		try{
			return dao.getNovoChecklistHolder(codUnidade);
		}catch(SQLException e){
			e.printStackTrace();
			return new NovoChecklistHolder();
		}
	}
	
	public boolean update(Checklist checklist) {
		try {
			return dao.update(checklist);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean delete(Long codigo) {
		try {
			return dao.delete(codigo);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
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
			Long codUnidade, long limit, long offset) {
		try {
			return dao.getAll(dataInicial, dataFinal, equipe, codUnidade, limit, offset);
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
