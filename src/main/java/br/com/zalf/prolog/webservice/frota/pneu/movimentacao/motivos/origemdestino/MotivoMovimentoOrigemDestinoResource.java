package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.OrigemDestinoListagem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino._model.MotivoMovimentoOrigemDestinoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino._model.MotivoMovimentoOrigemDestinoListagem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino._model.MotivoMovimentoOrigemDestinoListagemMotivos;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino._model.MotivoMovimentoOrigemDestinoVisualizacao;
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
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Secured
@Path("/movimentacoes/motivos/origens-destinos")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class MotivoMovimentoOrigemDestinoResource {
    @NotNull
    private final MotivoMovimentoOrigemDestinoService motivoMovimentoOrigemDestinoService =
            new MotivoMovimentoOrigemDestinoService();
    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @UsedBy(platforms = {Platform.WEBSITE})
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO})
    public Response insert(@NotNull @Valid @Required final List<MotivoMovimentoOrigemDestinoInsercao> unidades) {
        motivoMovimentoOrigemDestinoService.insert(
                unidades,
                colaboradorAutenticadoProvider.get().getCodigo());
        return Response.ok("Informações salvas com sucesso");
    }

    @GET
    @Path("/{codMotivoOrigemDestino}")
    @UsedBy(platforms = {Platform.WEBSITE})
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE})
    public MotivoMovimentoOrigemDestinoVisualizacao getMotivoOrigemDestino(
            @NotNull @PathParam("codMotivoOrigemDestino") final Long codMotivoOrigemDestino) {
        return motivoMovimentoOrigemDestinoService.getMotivoOrigemDestino(
                codMotivoOrigemDestino,
                colaboradorAutenticadoProvider.get().getZoneIdUnidadeColaborador());
    }

    @GET
    @UsedBy(platforms = {Platform.WEBSITE})
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE})
    public List<MotivoMovimentoOrigemDestinoListagem> getMotivosOrigemDestino() {
        return motivoMovimentoOrigemDestinoService.getMotivosOrigemDestino(colaboradorAutenticadoProvider.get().getCodigo());
    }

    @GET
    @UsedBy(platforms = {Platform.ANDROID})
    @Path("/listagem-resumida")
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE})
    public MotivoMovimentoOrigemDestinoListagemMotivos getMotivosByOrigemAndDestinoAndUnidade(
            @QueryParam("origemMovimento") @NotNull final OrigemDestinoEnum origemMovimento,
            @QueryParam("destinoMovimento") @NotNull final OrigemDestinoEnum destinoMovimento,
            @QueryParam("codUnidade") @NotNull final Long codUnidade) {
        return motivoMovimentoOrigemDestinoService.getMotivosByOrigemAndDestinoAndUnidade(
                origemMovimento,
                destinoMovimento,
                codUnidade);
    }

    @GET
    @Path("/transicoes-existentes")
    @UsedBy(platforms = {Platform.ANDROID})
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE})
    public List<OrigemDestinoListagem> getTransicoesExistentesByUnidade(
            @QueryParam("codUnidade") @NotNull final Long codUnidade) {
        return motivoMovimentoOrigemDestinoService.getTransicoesExistentesByUnidade(codUnidade);
    }

}
