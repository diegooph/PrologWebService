package br.com.zalf.prolog.webservice.interceptors.auth.authorization;

import br.com.zalf.prolog.webservice.autenticacao.token.TokenCleaner;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Data
public final class AuthMethod {
    @NotNull
    private final AuthType authType;
    @NotNull
    @Getter(AccessLevel.NONE)
    private final String authorizationHeaderValue;

    @NotNull
    public String getOnlyTokenPart() {
        return authType == AuthType.BEARER
                ? TokenCleaner.getOnlyToken(authorizationHeaderValue)
                : authorizationHeaderValue;
    }
}
