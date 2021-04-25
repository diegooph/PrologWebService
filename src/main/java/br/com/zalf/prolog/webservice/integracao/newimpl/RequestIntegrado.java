package br.com.zalf.prolog.webservice.integracao.newimpl;

import br.com.zalf.prolog.webservice.autenticacao.token.TokenCleaner;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Created on 2021-04-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component
public final class RequestIntegrado {

    @NotNull
    public String getRequestToken() {
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        final String authorization = requestAttributes.getRequest().getHeader("Authorization");
        return TokenCleaner.getOnlyToken(authorization);
    }
}
