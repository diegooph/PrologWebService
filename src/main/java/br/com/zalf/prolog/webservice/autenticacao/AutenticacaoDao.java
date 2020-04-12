package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Autenticação do usuário no sistema.
 */
public interface AutenticacaoDao {
    /**
     * Cria um novo token para o usuário. Esse Token será utilizado para fazer a validação
     * do usuário que está requisitando informações do servidor.
     *
     * @param cpf CPF do usuário.
     * @return Objeto {@link Autenticacao autenticação} com o token gerado e o status do login.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    Autenticacao insertOrUpdate(@NotNull final Long cpf) throws Throwable;

    /**
     * Cria um novo token para o usuário. Esse Token será utilizado para fazer a validação
     * do usuário que está requisitando informações do servidor.
     *
     * @param codColaborador Código do colaborador que será autenticado
     * @return Objeto {@link Autenticacao autenticação} com o token gerado e o status do login.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    Autenticacao insertOrUpdateByCodColaborador(@NotNull final Long codColaborador) throws Throwable;

    /**
     * Deleta um token da tabela. Este método é usado quando o usuário realiza logout do sistema.
     *
     * @param token Token a ser deletado da tabela.
     * @return Resultado da operação, true ou false.
     * @throws Throwable Caso ocorra algum erro.
     */
    boolean delete(@NotNull final String token) throws Throwable;

    /**
     * Verifica a existência de um token.
     *
     * @param token                O token que queremos verificar.
     * @param apenasUsuariosAtivos Indica se devemos considerar na verificação apenas usuário
     *                             que estão ativados no sistema (STATUS_ATIVO = true).
     * @return {@link Optional optional} que irá conter o colaborador autenticado caso ele exista no sistema
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    Optional<ColaboradorAutenticado> verifyIfTokenExists(@NotNull final String token,
                                                         final boolean apenasUsuariosAtivos) throws Throwable;

    /**
     * Verifica a existência de um CPF e data de nascimento na base de dados.
     *
     * @param cpf                  CPF do colaborador a ser verificada a existência.
     * @param dataNascimento       Data de Nascimento do colaborador a ser verificada a existência.
     * @param apenasUsuariosAtivos Indica se devemos considerar na verificação apenas usuário
     *                             que estão ativados no sistema (STATUS_ATIVO = true).
     * @return {@link Optional optional} que irá conter o colaborador autenticado caso ele exista no sistema.
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    Optional<ColaboradorAutenticado> verifyIfUserExists(@NotNull final Long cpf,
                                                        @NotNull final LocalDate dataNascimento,
                                                        final boolean apenasUsuariosAtivos) throws Throwable;

    /**
     * Verifica se o usuário tem as permissões necessárias para acessar determinada função.
     *
     * @param token                     O Token do usuário.
     * @param permissions               As permissões que esse token precisa ter.
     * @param needsToHaveAllPermissions Um valor booleano informando se o usuário precisa ter todas
     *                                  as permissões passadas no array no mínimo uma.
     * @param apenasUsuariosAtivos      Indica se devemos considerar na verificação apenas usuário
     *                                  que estão ativados no sistema (STATUS_ATIVO = true).
     * @return {@link Optional optional} que irá conter o colaborador autenticado caso ele exista no sistema
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    Optional<ColaboradorAutenticado> userHasPermission(@NotNull final String token,
                                                       @NotNull final int[] permissions,
                                                       final boolean needsToHaveAllPermissions,
                                                       final boolean apenasUsuariosAtivos) throws Throwable;

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
     * @return {@link Optional optional} que irá conter o colaborador autenticado caso ele exista no sistema
     * @throws Throwable Caso ocorra algum erro.
     */
    @NotNull
    Optional<ColaboradorAutenticado> userHasPermission(final long cpf,
                                                       @NotNull final LocalDate dataNascimento,
                                                       @NotNull int[] permissions,
                                                       final boolean needsToHaveAllPermissions,
                                                       final boolean apenasUsuariosAtivos) throws Throwable;
}
