package br.com.zalf.prolog.webservice.autenticacao;

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

	public boolean verifyIfTokenExists(String token, boolean apenasUsuariosAtivos){
		try {
			return dao.verifyIfTokenExists(token, apenasUsuariosAtivos);
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}

	public boolean verifyIfUserExists(long cpf, long dataNascimento, boolean apenasUsuariosAtivos) {
		try {
			return dao.verifyIfUserExists(cpf, dataNascimento, apenasUsuariosAtivos);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean userHasPermission(String token, int[] permissions,
									 boolean needsToHaveAllPermissions, boolean apenasUsuariosAtivos) {
		try {
			return dao.userHasPermission(token, permissions, needsToHaveAllPermissions, apenasUsuariosAtivos);
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}

	public boolean userHasPermission(long cpf, long dataNascimento, int[] permissions, boolean needsToHaveAllPermissions,
									 boolean apenasUsuariosAtivos) {
		try {
			return dao.userHasPermission(cpf, dataNascimento, permissions, needsToHaveAllPermissions, apenasUsuariosAtivos);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
