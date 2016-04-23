package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;

import br.com.zalf.prolog.models.Autenticacao;
import br.com.zalf.prolog.webservice.dao.AutenticacaoDaoImpl;
import br.com.zalf.prolog.webservice.dao.interfaces.AutenticacaoDao;

public class AutenticacaoService {
	private AutenticacaoDao dao = new AutenticacaoDaoImpl();
	
	public Autenticacao insertOrUpdate(Long cpf) {
		try {
			return dao.insertOrUpdate(cpf);					
		} catch (SQLException e) {
			e.printStackTrace();
			return new Autenticacao(Autenticacao.ERROR, cpf, "-1");
		}
	}
	
	public boolean delete(String token) {
	try {
		return dao.delete(token);
	} catch (SQLException e) {
		e.printStackTrace();
		return false;
	}
}
	
	public boolean verifyIfExists(Autenticacao autenticacao) {
		try {
			return dao.verifyIfExists(autenticacao);					
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean verifyIfTokenExists(String token){
		try {
			return dao.verifyIfTokenExists(token);
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	
}
