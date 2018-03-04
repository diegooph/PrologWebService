package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import com.sun.istack.internal.NotNull;

import javax.ws.rs.NotAuthorizedException;
import java.util.Base64;

public final class BasicAuthenticator extends ProLogAuthenticator {

    BasicAuthenticator(AutenticacaoService service) {
        super(service);
    }

    @Override
    public void validate(@NotNull final String value,
                         @NotNull final int[] permissions,
                         final boolean needsToHaveAllPermissions,
                         final boolean considerOnlyActiveUsers) throws NotAuthorizedException {
        final String[] cpfDataNascimento = new String(Base64.getDecoder().decode(value.getBytes())).split(":");
        if (cpfDataNascimento.length != 2) {
            throw new NotAuthorizedException("Usuário não tem permissão para utilizar esse método");
        }

        if (permissions.length == 0) {
            if (!service.verifyIfUserExists(
                    Long.parseLong(cpfDataNascimento[0]),
                    cpfDataNascimento[1],
                    considerOnlyActiveUsers)) {
                throw new NotAuthorizedException("Usuário não tem permissão para utilizar esse método");
            }
        } else {
            if (!service.userHasPermission(
                    Long.parseLong(cpfDataNascimento[0]),
                    cpfDataNascimento[1],
                    permissions,
                    needsToHaveAllPermissions,
                    considerOnlyActiveUsers)) {
                throw new NotAuthorizedException("Usuário não tem permissão para utilizar esse método");
            }
        }
    }
}