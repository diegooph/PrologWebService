package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import java.util.Base64;

public final class BasicAuthenticator extends ProLogAuthenticator {

    BasicAuthenticator(@NotNull final AutenticacaoService service) {
        super(service);
    }

    @Override
    public void validate(@NotNull final String value,
                         @NotNull final int[] permissions,
                         final boolean needsToHaveAllPermissions,
                         final boolean considerOnlyActiveUsers) throws NotAuthorizedException, ForbiddenException {
        final String[] cpfDataNascimento = new String(Base64.getDecoder().decode(value.getBytes())).split(":");
        if (cpfDataNascimento.length != 2) {
            throw new NotAuthorizedException("Autenticação não reconhecida");
        }

        if (permissions.length == 0) {
            if (!service.verifyIfUserExists(
                    Long.parseLong(cpfDataNascimento[0]),
                    cpfDataNascimento[1],
                    considerOnlyActiveUsers)) {
                throw new NotAuthorizedException("Autenticação inválida, usuário não encontrado");
            }
        } else {
            final StatusSecured statusSecured = service.userHasPermission(
                    Long.parseLong(cpfDataNascimento[0]),
                    cpfDataNascimento[1],
                    permissions,
                    needsToHaveAllPermissions,
                    considerOnlyActiveUsers);

            switch (statusSecured){
                case TOKEN_INVALIDO:
                    throw new NotAuthorizedException("Autenticação inválida, usuário não encontrado");
                case TOKEN_OK_SEM_PERMISSAO:
                    throw new ForbiddenException("Usuário não tem permissão para utilizar esse método");
            }
        }
    }
}