package br.com.zalf.prolog.webservice.config;

import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenCleaner;
import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Component
public final class CurrentRequest {
    public boolean isFromApi() {
        return getRequestTokenFromApi().isPresent();
    }

    @NotNull
    public Optional<String> getRequestToken() {
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        final String authorization = requestAttributes.getRequest().getHeader("Authorization");
        return authorization == null ? Optional.empty() : Optional.of(TokenCleaner.getOnlyToken(authorization));
    }

    @NotNull
    public Optional<String> getRequestTokenFromApi() {
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        final String authorization =
                requestAttributes.getRequest().getHeader(PrologCustomHeaders.HEADER_TOKEN_INTEGRACAO);
        return Optional.ofNullable(authorization);
    }
}