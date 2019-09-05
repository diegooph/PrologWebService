package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;

public final class BearerAuthenticator extends ProLogAuthenticator {
    private static final String TAG = BearerAuthenticator.class.getSimpleName();

    BearerAuthenticator(@NotNull final AutenticacaoService service) {
        super(service);
    }

    @Override
    public void validate(@NotNull final String value,
                         @NotNull final int[] permissions,
                         final boolean needsToHaveAllPermissions,
                         final boolean considerOnlyActiveUsers) throws NotAuthorizedException, ForbiddenException {
        Log.d(TAG, "Token: " + value);
        if (permissions.length == 0) {
            if (!service.verifyIfTokenExists(value, considerOnlyActiveUsers))
                throw new NotAuthorizedException("Autenticação inválida, usuário não encontrado");
        } else {
            final StatusSecured statusSecured = service.userHasPermission(
                    value,
                    permissions,
                    needsToHaveAllPermissions,
                    considerOnlyActiveUsers);

            switch (statusSecured){
                case TOKEN_INVALIDO:
                    throw new NotAuthorizedException("Autenticação inválida, usuário não encontrado");
                case TOKEN_OK_SEM_PERMISSAO:
                    throw new ForbiddenException("Usuário não tem permissão para utilizar esse método");
            }
        }
    }
}