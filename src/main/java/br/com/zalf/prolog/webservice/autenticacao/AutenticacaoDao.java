package br.com.zalf.prolog.webservice.autenticacao;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Autenticação do usuário no sistema.
 */
public interface AutenticacaoDao {
    /**
     * Cria um novo token para o usuário. Esse Token será utilizado para fazer a validação
     * do usuário que está requisitando informações do servidor.
     *
     * @param cpf Cpf do usuário.
     * @return Objeto {@link Autenticacao autenticação} com o token gerado e o status do login.
     * @throws SQLException Caso não seja possível criar o token na tabela.
     */
    @NotNull
    Autenticacao insertOrUpdate(@NotNull final Long cpf) throws SQLException;

    /**
     * Deleta um token da tabela. Este método é usado quando o usuário
     * solicita logout do sistema.
     *
     * @param token Contém o CPF e o token a ser deletado da tabela.
     * @return Resultado da operação, true ou false.
     * @throws SQLException Caso não seja possível fazer a deleção do token na tabela.
     */
    boolean delete(@NotNull final String token) throws SQLException;

    /**
     * Verifica a existência de um token.
     *
     * @param token                O token que queremos verificar.
     * @param apenasUsuariosAtivos Indica se devemos considerar na verificação apenas usuário
     *                             que estão ativados no sistema (STATUS_ATIVO = true).
     * @return Boolean com o resultado da requisição.
     * @throws SQLException Caso não seja possível realizar a busca.
     */
    boolean verifyIfTokenExists(@NotNull final String token, final boolean apenasUsuariosAtivos) throws SQLException;

    /**
     * Verifica a existência de um CPF e data de nascimento na base de dados.
     *
     * @param cpf                  CPF do colaborador a ser verificada a existência.
     * @param dataNascimento       Data de Nascimento do colaborador a ser verificada a existência.
     * @param apenasUsuariosAtivos Indica se devemos considerar na verificação apenas usuário
     *                             que estão ativados no sistema (STATUS_ATIVO = true).
     * @return Valor booleano que representa se o usuário está cadastrado no banco de dados.
     * @throws SQLException Caso não seja possível verificar a existência no banco de dados.
     */
    boolean verifyIfUserExists(@NotNull final Long cpf,
                               @NotNull final LocalDate dataNascimento,
                               final boolean apenasUsuariosAtivos) throws SQLException;

    /**
     * Verifica se o usuário tem as permissões necessárias para acessar determinada função.
     *
     * @param token                     O Token do usuário.
     * @param permissions               As permissões que esse token precisa ter.
     * @param needsToHaveAllPermissions Um valor booleano informando se o usuário precisa ter todas
     *                                  as permissões passadas no array no mínimo uma.
     * @param apenasUsuariosAtivos      Indica se devemos considerar na verificação apenas usuário
     *                                  que estão ativados no sistema (STATUS_ATIVO = true).
     * @return Verdadeiro se o usuário tem acesso a uma ou todas as permissões passadas; caso contrário falso.
     * @throws SQLException Caso não seja possível consultar as permissões no banco de dados.
     */
    boolean userHasPermission(@NotNull final String token,
                              @NotNull final int[] permissions,
                              final boolean needsToHaveAllPermissions,
                              final boolean apenasUsuariosAtivos) throws SQLException;

    /**
     * Verifica se o usuário tem as permissões necessárias para acessar determinada função.
     *
     * @param cpf                       CPF do colaborador.
     * @param dataNascimento            Data de Nascimento do colaborador.
     * @param permissions               As permissões que esse CPF precisa ter.
     * @param needsToHaveAllPermissions Um valor booleano informando se o usuário precisa ter todas
     *                                  as permissões passadas no array ou mínimo uma.
     * @param apenasUsuariosAtivos      Indica se devemos considerar na verificação apenas usuário
     *                                  que estão ativados no sistema (STATUS_ATIVO = true).
     * @return Verdadeiro se o usuário tem acesso a uma ou todas as permissões passadas; caso contrário falso.
     * @throws SQLException Caso não seja possível consultar as permissões no banco de dados.
     */
    boolean userHasPermission(final long cpf,
                              @NotNull final LocalDate dataNascimento,
                              @NotNull int[] permissions,
                              final boolean needsToHaveAllPermissions,
                              final boolean apenasUsuariosAtivos) throws SQLException;
}
