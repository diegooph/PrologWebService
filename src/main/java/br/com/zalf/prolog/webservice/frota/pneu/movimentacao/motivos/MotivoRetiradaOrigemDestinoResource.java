package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
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
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Secured
@Path("/motivos/motivoOrigemDestino")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class MotivoRetiradaOrigemDestinoResource {
    @NotNull
    private final MotivoRetiradaOrigemDestinoService motivoRetiradaOrigemDestinoService =
            new MotivoRetiradaOrigemDestinoService();
    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @UsedBy(platforms = {Platform.WEBSITE})
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO})
    public Response insert(@NotNull @Valid @Required final List<MotivoRetiradaOrigemDestinoInsercao> unidades) {
        motivoRetiradaOrigemDestinoService.insert(
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
    public MotivoRetiradaOrigemDestinoVisualizacao getMotivoOrigemDestino(
            @NotNull @PathParam("codMotivoOrigemDestino") final Long codMotivoOrigemDestino) {
        return motivoRetiradaOrigemDestinoService.getMotivoOrigemDestino(
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
    public List<MotivoRetiradaOrigemDestinoListagem> getMotivosOrigemDestino() {
        return motivoRetiradaOrigemDestinoService.getMotivosOrigemDestino(colaboradorAutenticadoProvider.get().getCodigo());
    }

    @GET
    @UsedBy(platforms = {Platform.ANDROID})
    @Path("/listagemResumida")
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE})
    public MotivoRetiradaOrigemDestinoListagemMotivos getMotivosByOrigemAndDestinoAndUnidade(
            @QueryParam("origemMovimento") @NotNull final OrigemDestinoEnum origemMovimento,
            @QueryParam("destinoMovimento") @NotNull final OrigemDestinoEnum destinoMovimento,
            @QueryParam("codUnidade") @NotNull final Long codUnidade) {
        return motivoRetiradaOrigemDestinoService.getMotivosByOrigemAndDestinoAndUnidade(
                origemMovimento,
                destinoMovimento,
                codUnidade);
    }

    @GET
    @Path("/unidade/{codUnidade}")
    @UsedBy(platforms = {Platform.ANDROID})
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.CADASTRAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.EDITAR_MOTIVOS_MOVIMENTACAO,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE})
    public List<OrigemDestinoListagem> getRotasExistentesByUnidade(@PathParam("codUnidade") @NotNull final Long codUnidade) {
        return motivoRetiradaOrigemDestinoService.getRotasExistentesByUnidade(codUnidade);
    }

}
