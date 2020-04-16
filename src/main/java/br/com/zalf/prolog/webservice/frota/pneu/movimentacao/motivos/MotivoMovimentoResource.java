package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
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
@Path("/movimentacoes/motivos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class MotivoMovimentoResource {
    @NotNull
    private final MotivoMovimentoService motivoMovimentoService = new MotivoMovimentoService();
    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO})
    public AbstractResponse insert(@Valid final MotivoMovimentoInsercao motivo) {
        return motivoMovimentoService.insert(motivo, colaboradorAutenticadoProvider.get().getCodigo());
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO})
    public Response update(@NotNull final MotivoMovimentoEdicao motivoMovimentoEdicao) {
        motivoMovimentoService.update(motivoMovimentoEdicao, colaboradorAutenticadoProvider.get().getCodigo());
        return Response.ok("Motivo atualizado com sucesso.");
    }

    @GET
    @Path("/{codMotivo}")
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO})
    public MotivoMovimentoVisualizacao getMotivoByCodigo(@NotNull @PathParam("codMotivo") final Long codMotivo) {
        return motivoMovimentoService.getMotivoByCodigo(
                codMotivo,
                colaboradorAutenticadoProvider.get().getZoneIdUnidadeColaborador());
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE})
    public List<MotivoMovimentoListagem> getMotivosListagem(@NotNull @QueryParam("codEmpresa") final Long codEmpresa,
                                                            @QueryParam("apenasAtivos") final boolean apenasAtivos) {
        return motivoMovimentoService.getMotivosListagem(
                codEmpresa,
                apenasAtivos,
                colaboradorAutenticadoProvider.get().getZoneIdUnidadeColaborador());
    }

    @GET
    @Path("/historicos")
    @UsedBy(platforms = {Platform.ANDROID})
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO})
    public List<MotivoMovimentoHistoricoListagem> getHistoricoByMotivoRetirada(
            @QueryParam("codMotivoRetirada") @NotNull final Long codMotivoRetirada) {
        return motivoMovimentoService.getHistoricoByMotivo(
                codMotivoRetirada,
                colaboradorAutenticadoProvider.get().getZoneIdUnidadeColaborador());
    }

}
