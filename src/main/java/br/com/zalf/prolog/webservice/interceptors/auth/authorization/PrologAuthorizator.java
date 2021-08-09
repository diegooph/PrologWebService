package br.com.zalf.prolog.webservice.interceptors.auth.authorization;

import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.Optional;

@AllArgsConstructor
public abstract class PrologAuthorizator {
    @NotNull
    protected final ContainerRequestContext requestContext;
    @NotNull
    protected final Secured secured;
    @NotNull
    protected final String authorizationHeader;

    @NotNull
    public abstract Optional<ColaboradorAutenticado> validate();
}