package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import org.jetbrains.annotations.NotNull;

public abstract class PrologApiAuthenticator implements AuthenticatorApi {
    @NotNull
    protected final BaseIntegracaoService service;

    PrologApiAuthenticator(@NotNull final BaseIntegracaoService service) {
        this.service = service;
    }
}