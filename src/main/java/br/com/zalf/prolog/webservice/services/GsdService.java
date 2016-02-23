package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Pergunta;
import br.com.zalf.prolog.models.gsd.Gsd;
import br.com.zalf.prolog.webservice.dao.GsdDaoImpl;

public class GsdService {
	private GsdDaoImpl dao = new GsdDaoImpl();
	
	public boolean insert(Gsd gsd) {
		try {
			return dao.insert(gsd);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
//	public boolean update(Gsd gsd) {
//		try {
//			return dao.update(gsd);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
	
//	public Gsd getByCod(Long codigo, String token) {
//		try {
//			return dao.getByCod(codigo, token);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	public List<Gsd> getByColaborador(Long cpf, String token) {
		try {
			return dao.getByColaborador(cpf, token);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public List<Gsd> getByAvaliador(Long cpf, String token) {
		try {
			return dao.getByAvaliador(cpf, token);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Gsd>();
		}
	}
	
	public List<Gsd> getAllExcetoAvaliador(Long cpf, String token) {
		try {
			return dao.getAllExcetoAvaliador(cpf, token);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Gsd>();
		}
	}
	
//	public List<Gsd> getAll() {
//		try {
//			return dao.getAll();
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return new ArrayList<Gsd>();
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
	
	public List<Pergunta> getPerguntas() {
		try {
			return dao.getPerguntas();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Pergunta>();
		}
	}
	
}
