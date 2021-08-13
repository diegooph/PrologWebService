package br.com.zalf.prolog.webservice.interceptors.auth.authorization;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.Optional;

@SuppressWarnings("NullableProblems")
public final class BearerAuthorizer extends PrologAuthorizer {

    public BearerAuthorizer(@NotNull final ContainerRequestContext requestContext,
                            @NotNull final Secured secured,
                            @NotNull final AuthMethod authMethod) {
        super(requestContext, secured, authMethod);
    }

    @NotNull
    @Override
    public Optional<ColaboradorAutenticado> validate() {
        return Optional.of(internalValidate(
                authMethod.getOnlyTokenPart(),
                secured.permissions(),
                secured.needsToHaveAllPermissions(),
                secured.considerOnlyActiveUsers()));
    }

    @NotNull
    private ColaboradorAutenticado internalValidate(@NotNull final String value,
                                                    @NotNull final int[] permissions,
                                                    final boolean needsToHaveAllPermissions,
                                                    final boolean considerOnlyActiveUsers) {
        final AutenticacaoService service = new AutenticacaoService();
        if (permissions.length == 0) {
            final Optional<ColaboradorAutenticado> optional =
                    service.verifyIfTokenExists(value, considerOnlyActiveUsers);
            return optional.orElseThrow(this::throwsNotAuthorized);
        } else {
            final Optional<ColaboradorAutenticado> optional = service.userHasPermission(
                    value,
                    permissions,
                    needsToHaveAllPermissions,
                    considerOnlyActiveUsers);
            final ColaboradorAutenticado colaboradorAutenticado = optional.orElseThrow(this::throwsNotAuthorized);
            throwsIfStatusNotSecured(colaboradorAutenticado.getStatusSecured());
            return colaboradorAutenticado;
        }
    }

    private void throwsIfStatusNotSecured(@NotNull final StatusSecured statusSecured) {
        if (statusSecured == StatusSecured.TOKEN_INVALIDO) {
            throwsNotAuthorized();
        } else if (statusSecured == StatusSecured.TOKEN_OK_SEM_PERMISSAO) {
            throwsForbidden();
        }
    }

    private RuntimeException throwsNotAuthorized() {
        throw new NotAuthorizedException("Autenticação inválida, usuário não encontrado");
    }

    private void throwsForbidden() {
        throw new ForbiddenException("Usuário não tem permissão para utilizar esse método");
    }
}