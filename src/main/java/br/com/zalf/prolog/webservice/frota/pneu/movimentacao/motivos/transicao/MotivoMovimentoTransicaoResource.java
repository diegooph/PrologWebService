package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.TransicaoExistenteUnidade;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.TransicaoVisualizacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.insercao.MotivoMovimentoTransicaoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.TransicaoUnidadeMotivos;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.UnidadeTransicoesMotivoMovimento;
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
@Path("/v2/movimentacoes/motivos/transicoes")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class MotivoMovimentoTransicaoResource {
    @NotNull
    private final MotivoMovimentoTransicaoService motivoMovimentoTransicaoService =
            new MotivoMovimentoTransicaoService();
    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @UsedBy(platforms = {Platform.WEBSITE})
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO})
    public Response insert(@NotNull @Valid @Required final List<MotivoMovimentoTransicaoInsercao> unidades) {
        motivoMovimentoTransicaoService.insert(
                unidades,
                colaboradorAutenticadoProvider.get().getCodigo());
        return Response.ok("Informações salvas com sucesso");
    }

    @GET
    @Path("/{codTransicao}")
    @UsedBy(platforms = {Platform.WEBSITE})
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE})
    public TransicaoVisualizacao getTransicaoVisualizacao(
            @NotNull @PathParam("codTransicao") final Long codTransicao) {
        return motivoMovimentoTransicaoService.getTransicaoVisualizacao(
                codTransicao,
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
    public List<UnidadeTransicoesMotivoMovimento> getUnidadesTransicoesMotivoMovimento() {
        return motivoMovimentoTransicaoService.getUnidadesTransicoesMotivoMovimento(
                colaboradorAutenticadoProvider.get().getCodigo());
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
    public TransicaoUnidadeMotivos getMotivosTransicaoUnidade(
            @QueryParam("origemMovimento") @NotNull final OrigemDestinoEnum origemMovimento,
            @QueryParam("destinoMovimento") @NotNull final OrigemDestinoEnum destinoMovimento,
            @QueryParam("codUnidade") @NotNull final Long codUnidade) {
        return motivoMovimentoTransicaoService.getMotivosTransicaoUnidade(
                origemMovimento,
                destinoMovimento,
                codUnidade);
    }

    @GET
    @Path("/existentes")
    @UsedBy(platforms = {Platform.ANDROID})
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE})
    public List<TransicaoExistenteUnidade> getTransicoesExistentesByUnidade(
            @QueryParam("codUnidade") @NotNull final Long codUnidade) {
        return motivoMovimentoTransicaoService.getTransicoesExistentesByUnidade(codUnidade);
    }

}
