package br.com.zalf.prolog.webservice.interceptors.debugenv;

import br.com.zalf.prolog.webservice.config.BuildConfig;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Created on 13/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Provider
@ResourceDebugOnly
public final class ResourceDebugOnlyInterceptor implements ContainerRequestFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        if (!BuildConfig.DEBUG) {
            throw new IllegalStateException("Esse resource só pode ser utilizado em ambientes de testes");
        }
    }
}