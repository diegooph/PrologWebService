package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.empresa.oprojeto.models.Colaborador;
import br.com.empresa.oprojeto.models.Funcao;
import br.com.zalf.prolog.webservice.dao.ColaboradorDaoImpl;

public class ColaboradorService {
	private ColaboradorDaoImpl dao = new ColaboradorDaoImpl();
	
	public boolean insert(Colaborador colaborador) {
		try {
			return dao.insert(colaborador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean update(Colaborador colaborador) {
		try {
			return dao.update(colaborador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean delete(Long cpf) {
		try {
			return dao.delete(cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Colaborador getByCod(Long cpf) {
		try {
			return dao.getByCod(cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Colaborador> getAll() {
		try {
			return dao.getAll();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Colaborador>();
		}
	}
	
	public Funcao getFuncaoByCod(Long codigo) {
		try {
			return dao.getFuncaoByCod(codigo);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean verifyLogin(long cpf, Date dataNascimento) {
		try {
			return dao.verifyLogin(cpf, dataNascimento);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
