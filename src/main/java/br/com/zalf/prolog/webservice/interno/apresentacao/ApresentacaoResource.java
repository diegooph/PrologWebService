package br.com.zalf.prolog.webservice.interno.apresentacao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created on 13/04/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/interno/apresentacao")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ApresentacaoResource {
    @NotNull
    private final ApresentacaoService service = new ApresentacaoService();

    @GET
    @Path("/reseta-clona-empresa")
    public Response getResetaClonaEmpresaApresentacao(
            @HeaderParam("Authorization") @Required final String authorization,
            @QueryParam("codEmpresaBase") @Required final Long codEmpresaBase,
            @QueryParam("codEmpresaUsuario") @Required final Long codEmpresaUsuario) throws ProLogException {

        return service.getResetaClonaEmpresaApresentacao(authorization, codEmpresaBase, codEmpresaUsuario);
    }
}