package br.com.empresa.oprojeto.webservice.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.empresa.oprojeto.models.gsd.Gsd;
import br.com.empresa.oprojeto.webservice.dao.GsdDaoImpl;

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
	
	public boolean update(Gsd gsd) {
		try {
			return dao.update(gsd);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Gsd getByCod(Long codigo) {
		try {
			return dao.getByCod(codigo);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Gsd> getByColaborador(Long cpf) {
		try {
			return dao.getByColaborador(cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public List<Gsd> getByAvaliador(Long cpf) {
		try {
			return dao.getByAvaliador(cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Gsd>();
		}
	}
	
	public List<Gsd> getAll() {
		try {
			return dao.getAll();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Gsd>();
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
	
}
