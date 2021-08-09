package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Created on 08/10/2020
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class ApiAuthenticator extends PrologAuthenticator {
    @NotNull
    private final BaseIntegracaoService service;

    ApiAuthenticator(@NotNull final RequestAuthenticator authenticator) {
        this.service = (BaseIntegracaoService) authenticator;
    }

    @NotNull
    @Override
    public Optional<ColaboradorAutenticado> validate(@NotNull final String token,
                                                     @Nullable final Secured secured) {
        internalValidade(token);
        return Optional.empty();
    }

    private void internalValidade(@NotNull final String apiToken) {
        service.ensureValidToken(apiToken);
    }
}