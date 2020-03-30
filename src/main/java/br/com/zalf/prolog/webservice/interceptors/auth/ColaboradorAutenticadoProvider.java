package br.com.zalf.prolog.webservice.interceptors.auth;

import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.core.Context;
import java.util.function.Supplier;

/**
 * Created on 2020-03-26
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ColaboradorAutenticadoProvider implements Supplier<ColaboradorAutenticado> {
    @Context
    private ContainerRequest containerRequest;

    @Override
    public ColaboradorAutenticado get() {
        return (ColaboradorAutenticado) containerRequest.getSecurityContext().getUserPrincipal();
    }
}
