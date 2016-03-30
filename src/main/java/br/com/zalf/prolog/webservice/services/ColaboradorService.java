package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.Funcao;
import br.com.zalf.prolog.models.Request;
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
	
	public boolean update(Long cpfAntigo, Colaborador colaborador) {
		try {
			return dao.update(cpfAntigo, colaborador);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean delete(Request<Colaborador> request) {
		try {
			return dao.delete(request);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Colaborador getByCod(Long cpf, String token) {
		try {
			return dao.getByCod(cpf, token);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Colaborador> getAtivosByUnidade(Long codUnidade, String token, Long cpf) {
		try {
			return dao.getAtivosByUnidade(codUnidade, token, cpf);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<Colaborador>();
		}
	}
	
	public List<Colaborador> getAll(Request<?> request) {
		try {
			return dao.getAll(request);
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
