package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import com.sun.istack.internal.NotNull;

import javax.ws.rs.NotAuthorizedException;

public final class BasicAuthenticator extends ProLogAuthenticator {

    BasicAuthenticator(AutenticacaoService service) {
        super(service);
    }

    @Override
    public void validate(@NotNull final String value,
                            @NotNull final int[] permissions,
                            final boolean needsToHaveAll) throws NotAuthorizedException {
    }
}