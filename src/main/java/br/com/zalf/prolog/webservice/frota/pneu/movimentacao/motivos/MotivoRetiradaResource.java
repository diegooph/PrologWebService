package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.*;
import org.jetbrains.annotations.NotNull;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2020-03-18
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Path("/motivos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class MotivoRetiradaResource {

    @NotNull
    private final MotivoRetiradaService motivoRetiradaService = new MotivoRetiradaService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Long insert(@Valid final MotivoRetiradaInsercao motivo,
                       @HeaderParam("Authorization") final String tokenAutorizacao) {
        return motivoRetiradaService.insert(motivo, tokenAutorizacao);
    }

    @GET
    @Path("/{codMotivo}")
    public MotivoRetiradaVisualizacao getMotivoByCodigo(@NotNull @PathParam("codMotivo") final Long codMotivo,
                                                        @HeaderParam("Authorization") final String tokenAutenticacao) {
        return motivoRetiradaService.getMotivoByCodigo(codMotivo, tokenAutenticacao);
    }

    @GET
    public List<MotivoRetiradaListagem> getMotivosListagem(@NotNull @QueryParam("codEmpresa") final Long codEmpresa,
                                                           @NotNull @HeaderParam("Authorization") final String tokenAutenticacao) {
        return motivoRetiradaService.getMotivosListagem(codEmpresa, tokenAutenticacao);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response update(@NotNull final MotivoRetiradaEdicao motivoRetiradaEdicao,
                           @HeaderParam("Authorization") @NotNull final String tokenAutenticacao) {
        motivoRetiradaService.update(motivoRetiradaEdicao, tokenAutenticacao);
        return Response.ok("Motivo atualizado com sucesso.");
    }

    @GET
    @Path("/historico")
    @UsedBy(platforms = {Platform.ANDROID})
    public List<MotivoRetiradaHistoricoListagem> getHistoricoByMotivoRetirada(@QueryParam("codMotivo") @NotNull final Long codMotivoRetirada,
                                                                              @HeaderParam("Authorization") @NotNull final String tokenAutenticacao) {
        return motivoRetiradaService.getHistoricoByMotivo(codMotivoRetirada, tokenAutenticacao);
    }

}
