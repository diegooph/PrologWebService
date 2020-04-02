package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import java.util.Base64;
import java.util.Optional;

public final class BasicAuthenticator extends ProLogAuthenticator {

    BasicAuthenticator(@NotNull final AutenticacaoService service) {
        super(service);
    }

    @NotNull
    @Override
    public ColaboradorAutenticado validate(@NotNull final String value,
                                           @NotNull final int[] permissions,
                                           final boolean needsToHaveAllPermissions,
                                           final boolean considerOnlyActiveUsers) {
        final String[] cpfDataNascimento = new String(Base64.getDecoder().decode(value.getBytes())).split(":");
        if (cpfDataNascimento.length != 2) {
            throw new NotAuthorizedException("Autenticação não reconhecida");
        }

        if (permissions.length == 0) {
            final Optional<ColaboradorAutenticado> colaboradorAutenticado = service.verifyIfUserExists(
                    Long.parseLong(cpfDataNascimento[0]),
                    cpfDataNascimento[1],
                    considerOnlyActiveUsers);
            if (!colaboradorAutenticado.isPresent()) {
                throw new NotAuthorizedException("Autenticação inválida, usuário não encontrado");
            }
            return colaboradorAutenticado.get();
        } else {
            final Optional<ColaboradorAutenticado> optional = service.userHasPermission(
                    Long.parseLong(cpfDataNascimento[0]),
                    cpfDataNascimento[1],
                    permissions,
                    needsToHaveAllPermissions,
                    considerOnlyActiveUsers);
            if (!optional.isPresent()) {
                throw new NotAuthorizedException("Autenticação inválida, usuário não encontrado");
            }
            final ColaboradorAutenticado colaboradorAutenticado = optional.get();
            switch (colaboradorAutenticado.getStatusSecured()) {
                case TOKEN_INVALIDO:
                    throw new NotAuthorizedException("Autenticação inválida, usuário não encontrado");
                case TOKEN_OK_SEM_PERMISSAO:
                    throw new ForbiddenException("Usuário não tem permissão para utilizar esse método");
            }
            return colaboradorAutenticado;
        }
    }
}