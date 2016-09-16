package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.commons.login.Autenticacao;

import java.sql.SQLException;

/**
 * Classe AutenticacaoService responsavel por comunicar-se com a interface DAO
 */
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

	public boolean verifyIfTokenExists(String token){
		try {
			return dao.verifyIfTokenExists(token);
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}
}
