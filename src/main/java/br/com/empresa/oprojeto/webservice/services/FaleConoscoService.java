package br.com.empresa.oprojeto.webservice.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.empresa.oprojeto.models.FaleConosco;
import br.com.empresa.oprojeto.webservice.dao.FaleConoscoDaoImpl;

public class FaleConoscoService {
	private FaleConoscoDaoImpl dao = new FaleConoscoDaoImpl();
	
	public boolean save(FaleConosco faleConosco) {
		try {
			return dao.save(faleConosco);
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
	
	public FaleConosco getByCod(Long codigo) {
		try {
			return dao.getByCod(codigo);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<FaleConosco> getAll() {
		try {
			return dao.getAll();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<FaleConosco>();
		}
	}
	
	public List<FaleConosco> getByColaborador(long cpf) {
		try {
			return dao.getByColaborador(cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<FaleConosco>();
		}
	}
}
