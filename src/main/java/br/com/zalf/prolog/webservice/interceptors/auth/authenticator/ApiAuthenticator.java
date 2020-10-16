package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 08/10/2020
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class ApiAuthenticator extends PrologApiAuthenticator {
    ApiAuthenticator(@NotNull final BaseIntegracaoService service) {
        super(service);
    }

    @Override
    public void validade(@NotNull final String value, @NotNull final String tag) {
        service.ensureValidToken(value, tag);
    }
}