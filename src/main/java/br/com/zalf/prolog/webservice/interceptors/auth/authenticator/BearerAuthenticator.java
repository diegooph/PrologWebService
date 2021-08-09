package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import java.util.Optional;

@SuppressWarnings("NullableProblems")
public final class BearerAuthenticator extends PrologAuthenticator {
    @NotNull
    private static final String TAG = BearerAuthenticator.class.getSimpleName();
    @NotNull
    private final AutenticacaoService service;

    BearerAuthenticator(@NotNull final RequestAuthenticator authenticator) {
        this.service = (AutenticacaoService) authenticator;
    }

    @NotNull
    @Override
    public Optional<ColaboradorAutenticado> validate(@NotNull final String token,
                                                     @Nullable final Secured secured) {
        if (secured == null) {
            throw new IllegalArgumentException("For bearer authentication the secured cannot be null!");
        }

        return Optional.of(internalValidate(
                token,
                secured.permissions(),
                secured.needsToHaveAllPermissions(),
                secured.considerOnlyActiveUsers()));
    }

    @NotNull
    private ColaboradorAutenticado internalValidate(@NotNull final String value,
                                                    @NotNull final int[] permissions,
                                                    final boolean needsToHaveAllPermissions,
                                                    final boolean considerOnlyActiveUsers) {
        Log.d(TAG, "Token: " + value);
        if (permissions.length == 0) {
            final Optional<ColaboradorAutenticado> colaboradorAutenticado =
                    service.verifyIfTokenExists(value, considerOnlyActiveUsers);
            if (colaboradorAutenticado.isEmpty()) {
                throw new NotAuthorizedException("Autenticação inválida, usuário não encontrado");
            }
            return colaboradorAutenticado.get();
        } else {
            final Optional<ColaboradorAutenticado> optional = service.userHasPermission(
                    value,
                    permissions,
                    needsToHaveAllPermissions,
                    considerOnlyActiveUsers);
            if (optional.isEmpty()) {
                throw new NotAuthorizedException("Autenticação inválida, usuário não encontrado");
            }
            final ColaboradorAutenticado colaboradorAutenticado = optional.get();
            switch (colaboradorAutenticado.getStatusSecured()) {
                case TOKEN_INVALIDO:
                    throw new NotAuthorizedException("Autenticação inválida, usuário não encontrado");
                case TOKEN_OK_SEM_PERMISSAO:
                    throw new ForbiddenException("Usuário não tem permissão para utilizar esse método");
            }
            return colaboradorAutenticado;
        }
    }
}