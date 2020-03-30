package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
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
@Path("/motivos")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class MotivoRetiradaResource {

    @NotNull
    private final MotivoRetiradaService motivoRetiradaService = new MotivoRetiradaService();

    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Secured(permissions = {Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO})
    public Long insert(@Valid final MotivoRetiradaInsercao motivo) {
        return motivoRetiradaService.insert(motivo, colaboradorAutenticadoProvider.get().getCodigo());
    }

    @GET
    @Path("/{codMotivo}")
    @Secured(permissions = {Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO})
    public MotivoRetiradaVisualizacao getMotivoByCodigo(@NotNull @PathParam("codMotivo") final Long codMotivo) {
        return motivoRetiradaService.getMotivoByCodigo(codMotivo, colaboradorAutenticadoProvider.get().getZoneIdUnidadeColaborador());
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE})
    public List<MotivoRetiradaListagem> getMotivosListagem(@NotNull @QueryParam("codEmpresa") final Long codEmpresa,
                                                           @NotNull @QueryParam("apenasAtivos") final boolean apenasAtivos) {
        return motivoRetiradaService.getMotivosListagem(codEmpresa, apenasAtivos, colaboradorAutenticadoProvider.get().getZoneIdUnidadeColaborador());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Secured(permissions = {Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE})
    public Response update(@NotNull final MotivoRetiradaEdicao motivoRetiradaEdicao) {
        motivoRetiradaService.update(motivoRetiradaEdicao, colaboradorAutenticadoProvider.get().getCodigo());
        return Response.ok("Motivo atualizado com sucesso.");
    }

    @GET
    @Path("/historico")
    @UsedBy(platforms = {Platform.ANDROID})
    @Secured(permissions = {Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE})
    public List<MotivoRetiradaHistoricoListagem> getHistoricoByMotivoRetirada(@QueryParam("codMotivoRetirada") @NotNull final Long codMotivoRetirada) {
        return motivoRetiradaService.getHistoricoByMotivo(codMotivoRetirada, colaboradorAutenticadoProvider.get().getZoneIdUnidadeColaborador());
    }

}
