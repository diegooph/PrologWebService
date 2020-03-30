package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 2020-03-18
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Secured
@Path("/motivos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class MotivoRetiradaResource {

    @NotNull
    private final MotivoRetiradaService motivoRetiradaService = new MotivoRetiradaService();

    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Long insert(@Valid final MotivoRetiradaInsercao motivo) {
        return motivoRetiradaService.insert(motivo, colaboradorAutenticadoProvider.get().getCodigo());
    }

    @GET
    @Path("/{codMotivo}")
    public MotivoRetiradaVisualizacao getMotivoByCodigo(@NotNull @PathParam("codMotivo") final Long codMotivo) {
        return motivoRetiradaService.getMotivoByCodigo(codMotivo, colaboradorAutenticadoProvider.get().getZoneIdUnidadeColaborador());
    }

    @GET
    public List<MotivoRetiradaListagem> getMotivosListagem(@NotNull @QueryParam("codEmpresa") final Long codEmpresa,
                                                           @NotNull @QueryParam("apenasAtivos") final boolean apenasAtivos) {
        return motivoRetiradaService.getMotivosListagem(codEmpresa, apenasAtivos, colaboradorAutenticadoProvider.get().getZoneIdUnidadeColaborador());
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
    public List<MotivoRetiradaHistoricoListagem> getHistoricoByMotivoRetirada(@QueryParam("codMotivoRetirada") @NotNull final Long codMotivoRetirada,
                                                                              @HeaderParam("Authorization") @NotNull final String tokenAutenticacao) {
        return motivoRetiradaService.getHistoricoByMotivo(codMotivoRetirada, tokenAutenticacao);
    }

}
