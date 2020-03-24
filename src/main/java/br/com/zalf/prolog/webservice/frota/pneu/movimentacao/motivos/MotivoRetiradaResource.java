package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaVisualizacao;
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
    public Response update(@NotNull final MotivoRetiradaEdicao motivoRetiradaEdicao,
                           @HeaderParam("Authorization") @NotNull final String tokenAutenticacao) {
        motivoRetiradaService.update(motivoRetiradaEdicao, tokenAutenticacao);
        return Response.ok("Motivo atualizado com sucesso.");
    }

    @DELETE
    @Path("/{codMotivo}")
    public Response delete(@PathParam("codMotivo") @NotNull final Long codMotivo,
                           @HeaderParam("Authorization") @NotNull final String tokenAutenticacao) {
        motivoRetiradaService.delete(codMotivo, tokenAutenticacao);
        return Response.ok("Motivo deletado com sucesso.");
    }

}
