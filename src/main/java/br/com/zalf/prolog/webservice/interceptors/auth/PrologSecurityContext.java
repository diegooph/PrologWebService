package br.com.zalf.prolog.webservice.interceptors.auth;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * Created on 2020-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class PrologSecurityContext implements SecurityContext {
    @NotNull
    private ColaboradorAutenticado colaboradorAutenticado;
    @NotNull
    private ApiAutenticado apiAutenticado;

    public PrologSecurityContext(@NotNull final ColaboradorAutenticado colaboradorAutenticado) {
        this.colaboradorAutenticado = colaboradorAutenticado;
    }

    public PrologSecurityContext(@NotNull final ApiAutenticado apiAutenticado) {
        this.apiAutenticado = apiAutenticado;
    }

    @Override
    public Principal getUserPrincipal() {
        return colaboradorAutenticado;
    }

    @Override
    public boolean isUserInRole(final String role) {
        return false;
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }

    @Override
    public String toString() {
        return "PrologSecurityContext{" +
                "colaboradorAutenticado=" + colaboradorAutenticado +
                '}';
    }
}