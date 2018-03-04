package br.com.zalf.prolog.webservice.autenticacao;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Autenticação do usuário no sistema.
 */
public interface AutenticacaoDao {
	/**
	 * Cria um novo token para o usuário.
	 *
	 * @param cpf cpf do usuário
	 * @return objeto autenticação com o token gerado e o status do login
	 * @throws SQLException caso não seja possível realizar o insert na tabela
	 */
	Autenticacao insertOrUpdate(Long cpf) throws SQLException;
	/**
	 * Verifica a existência de um token.
	 *
	 * @param token um token
	 * @param apenasUsuariosAtivos indica se devemos considerar na verificação apenas usuário
	 *                             que estão ativados no sistema (STATUS_ATIVO = true).
	 * @return boolean com o resultado da requisição
	 * @throws SQLException caso não seja possível realizar a busca
	 */
	boolean verifyIfTokenExists(String token, boolean apenasUsuariosAtivos) throws SQLException;

	/**
	 * Verifica a existência de um CPF e data de nascimento.
	 *
	 * @param cpf do colaborador a ser verificada a existência
	 * @param dataNascimento do colaborador a ser verificada a existência
	 * @param apenasUsuariosAtivos indica se devemos considerar na verificação apenas usuário
	 *                             que estão ativados no sistema (STATUS_ATIVO = true).
	 * @return valor booleano que representa se o usuário está cadastrado no banco de dados
	 * @throws SQLException caso não seja possível verificar a existência no banco de dados
	 */
	boolean verifyIfUserExists(Long cpf, LocalDate dataNascimento, boolean apenasUsuariosAtivos) throws SQLException;

	/**
	 * Verifica se o usuário tem as permissões necessárias para acessar determinada função.
	 *
	 * @param token um token
	 * @param permissions as permissões que esse token precisa ter
	 * @param needsToHaveAllPermissions um valor booleano informando se o usuário precisa ter todas
	 *                       			as permissões passadas no array ou apenas uma
	 * @param apenasUsuariosAtivos indica se devemos considerar na verificação apenas usuário
	 *                             que estão ativados no sistema (STATUS_ATIVO = true).
	 * @return verdadeiro se o usuário tem acesso a uma ou todas as permissões passadas; caso contrário falso.
	 */
	boolean userHasPermission(@NotNull String token, @NotNull int[] permissions,
							  boolean needsToHaveAllPermissions, boolean apenasUsuariosAtivos)
			throws SQLException;

	/**
	 * Verifica se o usuário tem as permissões necessárias para acessar determinada função.
	 * @param cpf um cpf
	 * @param dataNascimento uma data de nascimento
	 * @param permissions as permissões que esse cpf precisa ter
	 * @param needsToHaveAllPermissions um valor booleano informando se o usuário precisa ter todas
	 *                       as permissões passadas no array ou apenas uma.
	 * @param apenasUsuariosAtivos indica se devemos considerar na verificação apenas usuário
	 *                             que estão ativados no sistema (STATUS_ATIVO = true).
	 * @return verdadeiro se o usuário tem acesso a uma ou todas as permissões passadas; caso contrário falso.
	 */
	boolean userHasPermission(Long cpf, LocalDate dataNascimento, @NotNull int[] permissions,
							  boolean needsToHaveAllPermissions, boolean apenasUsuariosAtivos) throws SQLException;

	/**
	 * Deleta um token da tabela, usado quando o usuário solicita logout do sistema.
	 *
	 * @param token contém o CPF e o token a ser deletado da tabela
	 * @return resultado da operação, true ou false
	 * @throws SQLException caso não seja possível fazer a deleção do token na tabela
	 */
	boolean delete(String token) throws SQLException;
}
