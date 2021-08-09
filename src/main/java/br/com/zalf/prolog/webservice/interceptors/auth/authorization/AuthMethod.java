package br.com.zalf.prolog.webservice.interceptors.auth.authorization;

import br.com.zalf.prolog.webservice.autenticacao.token.TokenCleaner;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public final class AuthMethod {
    @NotNull
    private final AuthType authType;
    @NotNull
    private final String authorizationHeaderValue;

    @NotNull
    public String getOnlyTokenPart() {
        return authType == AuthType.BEARER
                ? TokenCleaner.getOnlyToken(authorizationHeaderValue)
                : authorizationHeaderValue;
    }
}
