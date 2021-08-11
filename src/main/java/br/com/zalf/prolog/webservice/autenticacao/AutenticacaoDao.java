package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Autenticação do usuário no sistema.
 */
@SuppressWarnings("NullableProblems")
public interface AutenticacaoDao {

    @NotNull
    Autenticacao createTokenByCpf(@NotNull final Long cpf) throws Throwable;

    @NotNull
    Autenticacao createTokenByCodColaborador(@NotNull final Long codColaborador) throws Throwable;

    boolean delete(@NotNull final String token) throws Throwable;

    @NotNull
    Optional<ColaboradorAutenticado> verifyIfTokenExists(@NotNull final String token,
                                                         final boolean apenasUsuariosAtivos) throws Throwable;
    @NotNull
    Optional<ColaboradorAutenticado> verifyIfUserExists(@NotNull final Long cpf,
                                                        @NotNull final LocalDate dataNascimento,
                                                        final boolean apenasUsuariosAtivos) throws Throwable;
    @NotNull
    Optional<ColaboradorAutenticado> userHasPermission(@NotNull final String token,
                                                       @NotNull final int[] permissions,
                                                       final boolean needsToHaveAllPermissions,
                                                       final boolean apenasUsuariosAtivos) throws Throwable;
    @NotNull
    Optional<ColaboradorAutenticado> userHasPermission(final long cpf,
                                                       @NotNull final LocalDate dataNascimento,
                                                       @NotNull int[] permissions,
                                                       final boolean needsToHaveAllPermissions,
                                                       final boolean apenasUsuariosAtivos) throws Throwable;
}
