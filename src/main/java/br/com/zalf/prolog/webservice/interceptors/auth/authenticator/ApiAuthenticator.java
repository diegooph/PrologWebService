package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.Optional;

/**
 * Created on 08/10/2020
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class ApiAuthenticator extends PrologAuthenticator {

    public ApiAuthenticator(@NotNull final ContainerRequestContext requestContext,
                            @NotNull final Secured secured,
                            @NotNull final String authorizationHeader) {
        super(requestContext, secured, authorizationHeader);
    }

    @NotNull
    @Override
    public Optional<ColaboradorAutenticado> validate() {
        internalValidade(authorizationHeader);
        return Optional.empty();
    }

    private void internalValidade(@NotNull final String apiToken) {
        new BaseIntegracaoService().ensureValidToken(apiToken);
    }
}