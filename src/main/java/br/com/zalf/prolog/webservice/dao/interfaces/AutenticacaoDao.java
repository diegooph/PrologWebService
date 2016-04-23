package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;

import br.com.zalf.prolog.models.Autenticacao;
/**
 * Autenticação do usuário no sistema.
 */
public interface AutenticacaoDao {
	/**
	 * Cria um novo token para o usuário
	 * @param cpf
	 * @return objeto autenticação com o token gerado e o status do login
	 * @throws SQLException caso não seja possível realizar o insert na tabela
	 */
	Autenticacao insertOrUpdate(Long cpf) throws SQLException;
	/**
	 * Verifica a existencia de um token para o cpf informado no objeto autenticação
	 * @param autenticacao contém o CPF e Token a serem verificados
	 * @return boolean com resultado do Exists, caso exista um CPF e Token compatíveis no BD
	 * retorna TRUE.
	 * @throws SQLException caso não seja possível verificar o BD
	 */
	boolean verifyIfExists(Autenticacao autenticacao) throws SQLException;
	/**
	 * Verifica a existência de um token
	 * @param token um token
	 * @return boolean com o resultado da requisição
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	public boolean verifyIfTokenExists(String token) throws SQLException;
	
	/**
	 * Deleta um token da tabela, usado quando o usuário solicita logout do sistema
	 * @param autenticacao contém o CPF e o token a ser deletado da tabela
	 * @return resultado do delete, true ou false
	 * @throws SQLException caso não seja possível fazer o delete do token na tabela
	 */
	boolean delete(String token) throws SQLException;
}
