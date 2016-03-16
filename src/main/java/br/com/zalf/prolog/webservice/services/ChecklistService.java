package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Pergunta;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.checklist.Checklist;
import br.com.zalf.prolog.webservice.dao.ChecklistDaoImpl;

public class ChecklistService {
	private ChecklistDaoImpl dao = new ChecklistDaoImpl();
	
	public boolean insert(Checklist checklist) {
		try {
			return dao.insert(checklist);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
//	
//	public boolean update(Checklist checklist) {
//		try {
//			return dao.update(checklist);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//	
//	public boolean delete(Long codigo) {
//		try {
//			return dao.delete(codigo);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
	
//	public Checklist getByCod(Long codigo, String token) {
//		try {
//			return dao.getByCod(codigo, token);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
//	public List<Checklist> getAll() {
//		try {
//			return dao.getAll();
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return new ArrayList<Checklist>();
//		}
//	}
	
	public List<Checklist> getAllByCodUnidade(Request<?> request, LocalDate dataInicial, LocalDate dataFinal, int limit, long offset) {
		try {
			return dao.getAllByCodUnidade(request, dataInicial, dataInicial, limit, offset);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Checklist>();
		}
	}
	

	public List<Checklist> getAllExcetoColaborador(Long cpf, long offset) {
		try {
			return dao.getAllExcetoColaborador(cpf, offset);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Checklist>();
		}
	}
	
	public List<Checklist> getByColaborador(Long cpf, String token, long offset) {
		try {
			return dao.getByColaborador(cpf, token, offset);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Checklist>();
		}
	}
	
	public List<Pergunta> getPerguntas() {
		try {
			System.out.println("pegando perguntas");
			return dao.getPerguntas();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Pergunta>();
		}
	}
}
