package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.Relato;
import br.com.zalf.prolog.webservice.dao.RelatoDaoImpl;

public class RelatoService {
	private RelatoDaoImpl dao = new RelatoDaoImpl();
	
	public boolean insert(Relato relato) {
		try {
			return dao.insert(relato);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean update(Relato relato) {
		try {
			return dao.update(relato);
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
	
	public Relato getByCod(Long codigo) {
		try {
			return dao.getByCod(codigo);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Relato> getAll() {
		try {
			return dao.getAll();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Relato>();
		}
	}
	
	public List<Relato> getByColaborador(Long cpf) {
		try {
			return dao.getByColaborador(cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Relato>();
		}
	}
	
	public List<Relato> getAllExcetoColaborador(Long cpf) {
		try {
			return dao.getAllExcetoColaborador(cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Relato>();
		}
	}
}
