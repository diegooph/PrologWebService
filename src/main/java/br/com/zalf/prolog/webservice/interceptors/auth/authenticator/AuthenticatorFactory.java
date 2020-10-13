package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public final class AuthenticatorFactory {

    private AuthenticatorFactory() {
        throw new IllegalStateException(AuthenticatorFactory.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static Authenticator createAuthenticator(@NotNull final AuthType authType,
                                                    @NotNull final AutenticacaoService service) {
        Preconditions.checkNotNull(authType);

        switch (authType) {
            case BASIC:
                return new BasicAuthenticator(service);
            case BEARER:
                return new BearerAuthenticator(service);
            default:
                throw new IllegalArgumentException();
        }
    }

    public static AuthenticatorApi createAuthenticatorApi(@NotNull final AuthType authType,
                                                          @NotNull final BaseIntegracaoService service) {
        Preconditions.checkNotNull(authType);

        if (authType == AuthType.API) {
            return new ApiAuthenticator(service);
        }
        throw new IllegalArgumentException();
    }
}