package br.com.zalf.prolog.webservice.interno.autenticacao._model;

import br.com.zalf.prolog.webservice.interceptors.auth.AuthType;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.NotAuthorizedException;
import java.util.Base64;

/**
 * Created on 2020-03-17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PrologInternalUserFactory {

    @NotNull
    public static PrologInternalUserAuthentication fromHeaderAuthorization(@NotNull final String headerAuthorization) {
        if (!headerAuthorization.startsWith(AuthType.BASIC.value())) {
            throw new NotAuthorizedException("headerAuthorization is not using BASIC Authentication!");
        }

        final String token = headerAuthorization.substring(AuthType.BASIC.value().length()).trim();
        final byte[] bytes = Base64.getDecoder().decode(token.getBytes());
        final String[] splitUsernamePassword = new String(bytes).split(":");
        if (splitUsernamePassword.length != 2) {
            throw new NotAuthorizedException("Autenticação não reconhecida!");
        }

        return PrologInternalUserAuthentication
                .builder()
                .username(splitUsernamePassword[0])
                .password(splitUsernamePassword[1])
                .build();
    }
}
