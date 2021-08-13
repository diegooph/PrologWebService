package br.com.zalf.prolog.webservice.interceptors.auth.authorization;

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
public final class ApiAuthorizer extends PrologAuthorizer {

    public ApiAuthorizer(@NotNull final ContainerRequestContext requestContext,
                         @NotNull final Secured secured,
                         @NotNull final AuthMethod authMethod) {
        super(requestContext, secured, authMethod);
    }

    @NotNull
    @Override
    public Optional<ColaboradorAutenticado> validate() {
        internalValidade(authMethod.getOnlyTokenPart());
        return Optional.empty();
    }

    private void internalValidade(@NotNull final String apiToken) {
        new BaseIntegracaoService().ensureValidToken(apiToken);
    }
}