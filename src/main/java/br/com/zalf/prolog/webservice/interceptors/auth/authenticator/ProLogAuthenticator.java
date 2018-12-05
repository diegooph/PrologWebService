package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public abstract class ProLogAuthenticator implements Authenticator {
    @NotNull
    protected final AutenticacaoService service;

    ProLogAuthenticator(@NotNull final AutenticacaoService service) {
        Preconditions.checkNotNull(service);
        this.service = service;
    }
}