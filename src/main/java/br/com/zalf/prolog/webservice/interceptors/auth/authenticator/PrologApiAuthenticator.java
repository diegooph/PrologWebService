package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public abstract class PrologApiAuthenticator implements AuthenticatorApi {
    @NotNull
    protected final BaseIntegracaoService service;

    PrologApiAuthenticator(@NotNull final BaseIntegracaoService service) {
        Preconditions.checkNotNull(service);
        this.service = service;
    }
}