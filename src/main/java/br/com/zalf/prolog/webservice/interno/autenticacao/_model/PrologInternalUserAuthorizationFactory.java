package br.com.zalf.prolog.webservice.interno.autenticacao._model;

import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenCleaner;
import br.com.zalf.prolog.webservice.interceptors.auth.authorization.AuthType;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.NotAuthorizedException;
import java.util.Base64;

/**
 * Created on 2020-03-17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PrologInternalUserAuthorizationFactory {

    @NotNull
    public static PrologInternalUserAuthorization fromHeaderAuthorization(@NotNull final String headerAuthorization) {
        if (!headerAuthorization.startsWith(AuthType.BASIC.value())
                && !headerAuthorization.startsWith(AuthType.BEARER.value())) {
            throw new NotAuthorizedException("We only support BASIC and BEARER authorization!");
        }

        if (headerAuthorization.startsWith(AuthType.BASIC.value())) {
            final String token = headerAuthorization.substring(AuthType.BASIC.value().length()).trim();
            final byte[] bytes = Base64.getDecoder().decode(token.getBytes());
            final String[] splitUsernamePassword = new String(bytes).split(":");
            if (splitUsernamePassword.length != 2) {
                throw new NotAuthorizedException("Autorização não reconhecida!");
            }

            return PrologInternalUserBasic
                    .builder()
                    .username(splitUsernamePassword[0])
                    .password(splitUsernamePassword[1])
                    .build();
        } else {
            return new PrologInternalUserBearer(TokenCleaner.getOnlyToken(headerAuthorization));
        }
    }
}
