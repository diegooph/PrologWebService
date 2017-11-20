package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.webservice.commons.util.Log;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * Classe AutenticacaoService responsavel por comunicar-se com a interface DAO
 */
public class AutenticacaoService {

	private AutenticacaoDao dao = new AutenticacaoDaoImpl();
	private static final String TAG = AutenticacaoService.class.getSimpleName();

	public Autenticacao insertOrUpdate(Long cpf) {
		try {
			return dao.insertOrUpdate(cpf);					
		} catch (SQLException e) {
			Log.e(TAG, String.format("Erro ao inserir o token para o cpf: %d", cpf), e);
			return new Autenticacao(Autenticacao.ERROR, cpf, "-1");
		}
	}

	public boolean delete(String token) {
		try {
			return dao.delete(token);
		} catch (SQLException e) {
			Log.e(TAG, String.format("Erro ao deletar o token: %s", token), e);
			return false;
		}
	}

	public boolean verifyIfTokenExists(String token, boolean apenasUsuariosAtivos){
		try {
			return dao.verifyIfTokenExists(token, apenasUsuariosAtivos);
		}catch (SQLException e){
			Log.e(TAG, String.format("Erro ao verificar se o token existe: %s", token), e);
			return false;
		}
	}

	public boolean verifyIfUserExists(long cpf, long dataNascimento, boolean apenasUsuariosAtivos) {
		try {
			return dao.verifyIfUserExists(cpf, dataNascimento, apenasUsuariosAtivos);
		} catch (SQLException e) {
			Log.e(TAG, String.format("Erro ao verificar se o usuário com os seguintes dados existe: cpf - %s |" +
					" Data de Nascimento - %d", cpf, dataNascimento), e);
			return false;
		}
	}

	public boolean userHasPermission(String token, int[] permissions,
									 boolean needsToHaveAllPermissions, boolean apenasUsuariosAtivos) {
		try {
			return dao.userHasPermission(token, permissions, needsToHaveAllPermissions, apenasUsuariosAtivos);
		}catch (SQLException e){
			Log.e(TAG, String.format("Erro ao verificar se o usuário com o token: %s tem acesso as permissões: %s |" +
					" needsToHaveAllPermissions/apenasUsuariosAtivos: %b/%b", token, Arrays.toString(permissions),
					needsToHaveAllPermissions, apenasUsuariosAtivos), e);
			return false;
		}
	}

	public boolean userHasPermission(long cpf, long dataNascimento, int[] permissions, boolean needsToHaveAllPermissions,
									 boolean apenasUsuariosAtivos) {
		try {
			return dao.userHasPermission(cpf, dataNascimento, permissions, needsToHaveAllPermissions, apenasUsuariosAtivos);
		} catch (SQLException e) {
			Log.e(TAG, String.format("Erro ao verificar se o usuário com o cpf/Nascimento: %d / %d tem acesso as permissões: %s |" +
							" needsToHaveAllPermissions/apenasUsuariosAtivos: %b/%b", cpf, dataNascimento, Arrays.toString(permissions),
					needsToHaveAllPermissions, apenasUsuariosAtivos), e);
			return false;
		}
	}
}
