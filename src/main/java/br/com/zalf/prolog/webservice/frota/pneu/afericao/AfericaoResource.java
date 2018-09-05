package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Optional;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.error.VersaoAppBloqueadaException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios.AfericaoRelatorioService;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.util.List;

/**
 * Created on 10/11/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/afericoes")
@DebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        implementation = DefaultAppVersionCodeHandler.class,
        targetVersionCode = 57,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class AfericaoResource {

    @NotNull
    private final AfericaoService service = new AfericaoService();

    @NotNull
    private final AfericaoRelatorioService relatorioService = new AfericaoRelatorioService();

    @POST
    @Secured(permissions = {
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO})
    @Path("/{codUnidade}")
    @UsedBy(platforms = Platform.ANDROID)
    public AbstractResponse insert(Afericao afericao,
                                   @PathParam("codUnidade") Long codUnidade,
                                   @HeaderParam("Authorization") String userToken) throws ProLogException {
        final Long codAfericao = service.insert(afericao, codUnidade, userToken);
        if (codAfericao != null) {
            return ResponseWithCod.ok("Aferição inserida com sucesso", codAfericao);
        } else {
            return Response.error("Erro ao inserir aferição");
        }
    }

    @GET
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA)
    @Path("/cronogramas/{codUnidade}")
    @UsedBy(platforms = Platform.ANDROID)
    public CronogramaAfericao getCronogramaAfericao(@PathParam("codUnidade") Long codUnidade,
                                                    @HeaderParam("Authorization") String userToken)
            throws ProLogException {
        return service.getCronogramaAfericao(codUnidade, userToken);
    }

    @GET
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO)
    @Path("/pneus-disponiveis-afericao-avulsa/unidades/{codUnidade}")
    @UsedBy(platforms = Platform.ANDROID)
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@HeaderParam("Authorization") @Required String userToken,
                                                           @PathParam("codUnidade") Long codUnidade)
            throws ProLogException {
        return service.getPneusAfericaoAvulsa(userToken, codUnidade);
    }

    @GET
    @Path("/unidades/{codUnidade}/nova-afericao-placa/{placaVeiculo}")
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA)
    @UsedBy(platforms = Platform.ANDROID)
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@PathParam("codUnidade") @Required Long codUnidade,
                                                  @PathParam("placaVeiculo") @Required String placa,
                                                  @QueryParam("tipoAfericao") @Required String tipoAfericao,
                                                  @HeaderParam("Authorization") @Required String userToken)
            throws ProLogException {
        return service.getNovaAfericaoPlaca(codUnidade, placa, tipoAfericao, userToken);
    }

    @GET
    @Path("/unidades/{codUnidade}/nova-afericao-avulsa/{codPneu}")
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO)
    @UsedBy(platforms = Platform.ANDROID)
    public NovaAfericaoAvulsa getNovaAfericaoAvulsa(@PathParam("codUnidade") @Required Long codUnidade,
                                                    @PathParam("codPneu") @Required Long codPneu,
                                                    @QueryParam("tipoAfericao") @Required String tipoAfericao)
            throws ProLogException {
        return service.getNovaAfericaoAvulsa(codUnidade, codPneu, tipoAfericao);
    }

    @GET
    @Path("/unidades/{codUnidade}/tipos-veiculos/{codTipoVeiculo}/placas/{placaVeiculo}")
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PLACA,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    public List<AfericaoPlaca> getAfericoesPlacas(
            @PathParam("codUnidade") Long codUnidade,
            @PathParam("codTipoVeiculo") String codTipoVeiculo,
            @PathParam("placaVeiculo") String placaVeiculo,
            @QueryParam("dataInicial") String dataInicial,
            @QueryParam("dataFinal") String dataFinal,
            @QueryParam("limit") int limit,
            @QueryParam("offset") long offset,
            @HeaderParam("Authorization") String userToken) throws ProLogException {
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

    @GET
    @Path("/unidades/{codUnidade}/avulsas")
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES,
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    public List<AfericaoAvulsa> getAfericoesAvulsas(
            @PathParam("codUnidade") Long codUnidade,
            @QueryParam("dataInicial") String dataInicial,
            @QueryParam("dataFinal") String dataFinal,
            @QueryParam("limit") int limit,
            @QueryParam("offset") long offset) throws ProLogException {
        return service.getAfericoesAvulsas(codUnidade, dataInicial, dataFinal, limit, offset);
    }

    @GET
    @Path("/unidades/{codUnidade}/avulsas-report")
    @Secured(permissions = {
            Pilares.Frota.Afericao.REALIZAR_AFERICAO_PNEU_AVULSO,
            Pilares.Frota.Afericao.VISUALIZAR_TODAS_AFERICOES})
    @UsedBy(platforms = Platform.ANDROID)
    public Report getAfericoesAvulsas(
            @PathParam("codUnidade") @Required final Long codUnidade,
            @QueryParam("codColaborador") @Optional final Long codColaborador,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal,
            @HeaderParam("Authorization") String userToken) throws ProLogException {
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
    public Afericao getByCod(@PathParam("codAfericao") Long codAfericao,
                             @PathParam("codUnidade") Long codUnidade,
                             @HeaderParam("Authorization") String userToken) throws ProLogException {
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
    public Restricao getRestricaoByCodUnidade(@PathParam("codUnidade") Long codUnidade) throws ProLogException {
        return service.getRestricaoByCodUnidade(codUnidade);
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
            @PathParam("codUnidade") Long codUnidade,
            @PathParam("codTipoVeiculo") String codTipoVeiculo,
            @PathParam("placaVeiculo") String placaVeiculo,
            @QueryParam("dataInicial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal,
            @QueryParam("limit") int limit,
            @QueryParam("offset") long offset,
            @HeaderParam("Authorization") String userToken) throws ProLogException {
        throw new VersaoAppBloqueadaException("Atualize o aplicativo para poder buscar as aferições realizadas");
    }

    @GET
    @Produces("application/csv")
    @Path("/unidades/{codUnidades}/csv")
    public StreamingOutput getDadosGeraisAfericao(
            @QueryParam("codUnidades") @Required final List<Long> codUnidades,
            @QueryParam("dataInicial") @Required final String dataInicial,
            @QueryParam("dataFinal") @Required final String dataFinal) {
        return outputStream -> relatorioService.getDadosGeraisAfericaoCsv(outputStream, codUnidades, dataInicial, dataFinal);
    }

    @GET
    @Path("/unidades/{codUnidades}/report")
    public Report getDadosGeraisProdutividadeReport(@QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                                    @QueryParam("dataInicial") @Required final String dataInicial,
                                                    @QueryParam("dataFinal") @Required final String dataFinal)
            throws ProLogException {
        return relatorioService.getDadosGeraisAfericaoReport(codUnidades, dataInicial, dataFinal);
    }
}