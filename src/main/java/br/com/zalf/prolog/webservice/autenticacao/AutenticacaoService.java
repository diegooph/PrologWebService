package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.commons.login.Autenticacao;

import java.sql.SQLException;

/**
 * Classe AutenticacaoService, responsavel por comunicarse com a camada DAO  
 */
public class AutenticacaoService {

	private AutenticacaoDao dao = new AutenticacaoDaoImpl();

	/**
	 * Insere ou recria uma autenticação de usuario
	 * @param cpf um cpf
	 * @return Autenticação com token gerado, ou token = -1 sinalizando erro
	 */
	public Autenticacao insertOrUpdate(Long cpf) {
		try {
			return dao.insertOrUpdate(cpf);					
		} catch (SQLException e) {
			e.printStackTrace();
			return new Autenticacao(Autenticacao.ERROR, cpf, "-1");
		}
	}

	/**
	 * Deleta o token de autenticação do usuario
	 * @param token um token
	 * @return True se deletado, false se não
	 */
	public boolean delete(String token) {
		try {
			return dao.delete(token);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Verifica a existencia de um token
	 * @param token recebe um token como parametro
	 * @return True caso token exista, False, se não existir
	 */
	public boolean verifyIfTokenExists(String token){
		try {
			return dao.verifyIfTokenExists(token);
		}catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}
}
