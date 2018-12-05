package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;

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
                         final boolean considerOnlyActiveUsers) throws NotAuthorizedException {
        Log.d(TAG, "Token: " + value);
        if (permissions.length == 0) {
            if (!service.verifyIfTokenExists(value, considerOnlyActiveUsers))
                throw new NotAuthorizedException("Usuário não tem permissão para utilizar esse método");
        } else {
            if (!service.userHasPermission(value, permissions, needsToHaveAllPermissions, considerOnlyActiveUsers))
                throw new NotAuthorizedException("Usuário não tem permissão para utilizar esse método");
        }
    }
}