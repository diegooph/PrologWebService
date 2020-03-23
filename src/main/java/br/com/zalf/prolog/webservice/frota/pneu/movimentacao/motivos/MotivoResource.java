package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoListagemApp;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoVisualizacaoListagem;
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
public final class MotivoResource {

    @NotNull
    private final MotivoService motivoService = new MotivoService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Long insert(@Valid final MotivoInsercao motivo,
                       @HeaderParam("Authorization") final String tokenAutorizacao) {
        return motivoService.insert(motivo, tokenAutorizacao);
    }

    @GET
    @Path("/{codMotivo}")
    public MotivoVisualizacaoListagem getMotivoByCodigo(@NotNull @PathParam("codMotivo") final Long codMotivo,
                                                        @HeaderParam("Authorization") final String tokenAutenticacao) {
        return motivoService.getMotivoByCodigo(codMotivo, tokenAutenticacao);
    }

    @GET
    public List<MotivoVisualizacaoListagem> getMotivosListagem(@NotNull @QueryParam("codEmpresa") final Long codEmpresa,
                                                               @NotNull @HeaderParam("Authorization") final String tokenAutenticacao) {
        return motivoService.getMotivosListagem(codEmpresa, tokenAutenticacao);
    }

    @PUT
    public Response update(@NotNull final MotivoEdicao motivoEdicao,
                           @HeaderParam("Authorization") @NotNull final String tokenAutenticacao) {
        motivoService.update(motivoEdicao, tokenAutenticacao);
        return Response.ok("Motivo atualizado com sucesso.");
    }

    @DELETE
    @Path("/{codMotivo}")
    public Response delete(@PathParam("codMotivo") @NotNull final Long codMotivo,
                           @HeaderParam("Authorization") @NotNull final String tokenAutenticacao) {
        motivoService.delete(codMotivo, tokenAutenticacao);
        return Response.ok("Motivo deletado com sucesso.");
    }

    @GET
    @UsedBy(platforms = {Platform.ANDROID})
    @Path("/{codEmpresa}")
    public List<MotivoListagemApp> getMotivosByOrigemAndDestino(@NotNull final OrigemDestinoEnum origem,
                                                                @NotNull final OrigemDestinoEnum destino,
                                                                @NotNull final Long codEmpresa) {
        return motivoService.getMotivosByOrigemAndDestino(origem, destino, codEmpresa);
    }

}
