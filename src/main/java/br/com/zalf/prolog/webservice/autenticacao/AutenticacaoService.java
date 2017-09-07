package br.com.zalf.prolog.webservice.autenticacao;

import java.sql.SQLException;
import java.util.Date;

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

	public boolean verifyLogin(long cpf, Date dataNascimento) {
		try {
			return dao.verifyLogin(cpf, dataNascimento);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean userHasPermission(String token, int[] permissions, boolean needsToHaveAll) {
		try {
			return dao.userHasPermission(token, permissions, needsToHaveAll);
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}

	public boolean userHasPermission(long cpf, long dataNascimento, int[] permissions, boolean needsToHaveAll) {
		try {
			return dao.userHasPermission(cpf, dataNascimento, permissions, needsToHaveAll);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
