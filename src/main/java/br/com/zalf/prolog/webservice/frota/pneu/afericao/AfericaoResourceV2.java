package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoParaRealizacao;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.VersaoAppBloqueadaException;
import br.com.zalf.prolog.webservice.frota.pneu._model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoBackwardHelper;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.Collections;
import java.util.List;

/**
 * Created on 10/11/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Controller
@Path("/v2/afericoes")
@ConsoleDebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        targetVersionCode = 64,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class AfericaoResourceV2 {
    @NotNull
    private final AfericaoServiceV2 service;

    @Autowired
    public AfericaoResourceV2(@NotNull final AfericaoServiceV2 service) {
        this.service = service;
    }

    @POST
    @Secured(permissions = {
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO})
    @Path("/{codUnidade}")
    @UsedBy(platforms = Platform.ANDROID)
    public AbstractResponse insert(@HeaderParam("Authorization") @Required final String userToken,
                                   @PathParam("codUnidade") @Required final Long codUnidade,
                                   @Required final Afericao afericao) throws ProLogException {
        afericao.setDataHora(Now.getLocalDateTimeUtc());
        final Long codAfericao = service.insertAfericao(codUnidade, afericao, true);
        return ResponseWithCod.ok("Aferição inserida com sucesso", codAfericao);
    }

    @GET
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA)
    @Path("/cronogramas/{codUnidade}")
    @UsedBy(platforms = Platform.ANDROID)
    @Deprecated
    public CronogramaAfericao getCronogramaAfericao(
            @HeaderParam("Authorization") @Required final String userToken,
            @PathParam("codUnidade") @Required final Long codUnidade) throws ProLogException {
        return getCronogramaAfericao(userToken, Collections.singletonList(codUnidade));
    }

    @GET
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA)
    @Path("/cronogramas")
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    public CronogramaAfericao getCronogramaAfericao(
            @HeaderParam("Authorization") @Required final String userToken,
            @QueryParam("codUnidades") @Required final List<Long> codUnidades) throws ProLogException {
        return service.getCronogramaAfericao(userToken, codUnidades);
    }

    @GET
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO)
    @Path("/pneus-disponiveis-afericao-avulsa/unidades/{codUnidade}")
    @UsedBy(platforms = Platform.ANDROID)
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@HeaderParam("Authorization") @Required final String userToken,
                                                           @PathParam("codUnidade") final Long codUnidade)
            throws ProLogException {
        return service.getPneusAfericaoAvulsa(userToken, codUnidade);
    }

    /**
     * @deprecated Este método foi depreciado, visto que, há uma versão com uma abstração usando
     * o body da requisição
     * para receber os dados:
     * <br>
     * {@link #getNovaAfericaoPlaca(String, Long, Long, String, TipoMedicaoColetadaAfericao)}
     * getNovaAfericaoPlaca(afericaoBuscaFiltro, userToken)}
     * <br>
     * Porém há sistemas dependentes desse endpoint ainda (WS, Android).
     */
    @Deprecated
    @GET
    @Path("/unidades/{codUnidade}/nova-afericao-placa/{placaVeiculo}")
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA)
    @UsedBy(platforms = Platform.ANDROID)
    public NovaAfericaoPlaca getNovaAfericaoPlaca(
            @HeaderParam("Authorization") @Required final String userToken,
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("placaVeiculo") @Required final String placa,
            @QueryParam("tipoAfericao") @Required final String tipoAfericao,
            @Context final SecurityContext securityContext) throws ProLogException {
        final ColaboradorAutenticado colaborador = (ColaboradorAutenticado) securityContext.getUserPrincipal();
        final Long codigoColaborador = colaborador.getCodigo();
        final Long codigoVeiculo = VeiculoBackwardHelper.getCodVeiculoByPlaca(codigoColaborador, placa);
        final TipoMedicaoColetadaAfericao tipoAfericaoEnum = TipoMedicaoColetadaAfericao.fromString(tipoAfericao);
        final AfericaoBuscaFiltro afericaoBusca =
                AfericaoBuscaFiltro.of(codUnidade, codigoVeiculo, placa, tipoAfericaoEnum);
        return service.getNovaAfericaoPlaca(afericaoBusca, userToken);
    }

    @GET
    @Path("/nova-afericao-placa")
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA)
    @UsedBy(platforms = Platform.ANDROID)
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@HeaderParam("Authorization") @Required final String userToken,
                                                  @QueryParam("codUnidade") @Required final Long codUnidade,
                                                  @QueryParam("codVeiculo") @Required final Long codVeiculo,
                                                  @QueryParam("placaVeiculo") @Required final String placaVeiculo,
                                                  @QueryParam("tipoAfericao") @Required final TipoMedicaoColetadaAfericao tipoAfericao)
            throws ProLogException {
        final AfericaoBuscaFiltro afericaoBusca =
                AfericaoBuscaFiltro.of(codUnidade, codVeiculo, placaVeiculo, tipoAfericao);
        return service.getNovaAfericaoPlaca(afericaoBusca, userToken);
    }

    @GET
    @Path("/unidades/{codUnidade}/nova-afericao-avulsa/{codPneu}")
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO)
    @UsedBy(platforms = Platform.ANDROID)
    public NovaAfericaoAvulsa getNovaAfericaoAvulsa(
            @HeaderParam("Authorization") @Required final String userToken,
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("codPneu") @Required final Long codPneu,
            @QueryParam("tipoAfericao") @Required final String tipoAfericao) throws ProLogException {
        return service.getNovaAfericaoAvulsa(userToken, codUnidade, codPneu, tipoAfericao);
    }

    /**
     * @deprecated <p>
     * Há nova versão em outra classe.
     * Segue método referencia: <br>
     * {@link br.com.zalf.prolog.webservice.v3.fleet.afericao.AfericaoResource#getAfericoesPlacas(List, String, String, Long, Long, boolean, int, int)}
     * </p>
     */
    @GET
    @Path("/unidades/{codUnidade}/tipos-veiculos/{codTipoVeiculo}/placas/{placaVeiculo}")
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Deprecated
    public List<AfericaoPlaca> getAfericoesPlacas(
            @PathParam("codUnidade") final Long codUnidade,
            @PathParam("codTipoVeiculo") final String codTipoVeiculo,
            @PathParam("placaVeiculo") final String placaVeiculo,
            @QueryParam("dataInicial") final String dataInicial,
            @QueryParam("dataFinal") final String dataFinal,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final long offset,
            @HeaderParam("Authorization") final String userToken) throws ProLogException {
        return service.getAfericoesPlacas(
                codUnidade,
                codTipoVeiculo,
                placaVeiculo,
                dataInicial,
                dataFinal,
                limit,
                offset,
                userToken);
    }

    /**
     * @deprecated <p>
     * Há nova versão em outra classe.
     * Segue método referencia: <br>
     * {@link AfericaoResource#getAfericoesAvulsas}
     * </p>
     */
    @GET
    @Path("/unidades/{codUnidade}/avulsas")
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Deprecated
    public List<AfericaoAvulsa> getAfericoesAvulsas(
            @PathParam("codUnidade") final Long codUnidade,
            @QueryParam("dataInicial") final String dataInicial,
            @QueryParam("dataFinal") final String dataFinal,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final long offset) throws ProLogException {
        return service.getAfericoesAvulsas(codUnidade, dataInicial, dataFinal, limit, offset);
    }

    /**
     * @deprecated <p>
     * Há nova versão em outra classe.
     * Segue método referencia: <br>
     * {@link AfericaoResource#getAfericoesAvulsas}
     * </p>
     */
    @GET
    @Path("/unidades/{codUnidade}/avulsas-report")
    @Secured(permissions = {
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO,
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES})
    @UsedBy(platforms = Platform.ANDROID)
    @Deprecated
    public Report getAfericoesAvulsas(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("codColaborador") @Optional final Long codColaborador,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal,
            @HeaderParam("Authorization") final String userToken) throws ProLogException {
        return service.getAfericoesAvulsas(userToken, codUnidade, codColaborador, dataInicial, dataFinal);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/{codAfericao}/unidades/{codUnidade}")
    public Afericao getByCod(@PathParam("codAfericao") final Long codAfericao,
                             @PathParam("codUnidade") final Long codUnidade,
                             @HeaderParam("Authorization") final String userToken) throws ProLogException {
        return service.getByCod(codUnidade, codAfericao, userToken);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/restricoes/{codUnidade}")
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    public Restricao getRestricaoByCodUnidade(@PathParam("codUnidade") final Long codUnidade) throws ProLogException {
        return service.getRestricaoByCodUnidade(codUnidade);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA})
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/campos-personalizados")
    public List<CampoPersonalizadoParaRealizacao> getCamposPersonalizadosRealizacao(
            @HeaderParam("Authorization") @Required final String userToken,
            @QueryParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("tipoProcessoColetaAfericao") @Required final TipoProcessoColetaAfericao tipoProcessoColetaAfericao)
            throws ProLogException {
        return service.getCamposPersonalizadosRealizacao(userToken, codUnidade, tipoProcessoColetaAfericao);
    }

    /**
     * @deprecated at 2018-08-21. Ainda mantemos aqui para poder lançar uma exception personalizada avisando para
     * atualizar o App.
     */
    @GET
    @Path("/{codUnidade}/{codTipoVeiculo}/{placaVeiculo}")
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    public List<Afericao> DEPRECATED_GET_AFERICOES(
            @PathParam("codUnidade") final Long codUnidade,
            @PathParam("codTipoVeiculo") final String codTipoVeiculo,
            @PathParam("placaVeiculo") final String placaVeiculo,
            @QueryParam("dataInicial") final long dataInicial,
            @QueryParam("dataFinal") final long dataFinal,
            @QueryParam("limit") final int limit,
            @QueryParam("offset") final long offset,
            @HeaderParam("Authorization") final String userToken) throws ProLogException {
        throw new VersaoAppBloqueadaException("Atualize o aplicativo para poder buscar as aferições realizadas");
    }
}