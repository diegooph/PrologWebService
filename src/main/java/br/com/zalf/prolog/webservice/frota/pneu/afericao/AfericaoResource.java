package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
        targetVersionCode = 55,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class AfericaoResource {

    private final AfericaoService service = new AfericaoService();

    @POST
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR)
    @Path("/{codUnidade}")
    public Response insert(AfericaoPlaca afericaoPlaca,
                           @PathParam("codUnidade") Long codUnidade,
                           @HeaderParam("Authorization") String userToken) {
        if (service.insert(afericaoPlaca, codUnidade, userToken)) {
            return Response.ok("Aferição inserida com sucesso");
        } else {
            return Response.error("Erro ao inserir aferição");
        }
    }

    @PUT
    @Secured
    public Response update(AfericaoPlaca afericaoPlaca) {
        if (service.updateKmAfericao(afericaoPlaca)) {
            return Response.ok("Km atualizado com sucesso");
        } else {
            return Response.error("Erro ao atualizar o KM");
        }
    }

    @GET
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR)
    @Path("/cronogramas/{codUnidade}")
    public CronogramaAfericao getCronogramaAfericao(@PathParam("codUnidade") Long codUnidade,
                                                    @HeaderParam("Authorization") String userToken) throws Exception {
        return service.getCronogramaAfericao(codUnidade, userToken);
    }

    @GET
    @Path("/unidades/{codUnidade}/nova-placa/{placaVeiculo}")
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR)
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@PathParam("codUnidade") @Required Long codUnidade,
                                                  @PathParam("placaVeiculo") @Required String placa,
                                                  @QueryParam("tipoAfericao") @Required String tipoAfericao,
                                                  @HeaderParam("Authorization") @Required String userToken) throws
            ProLogException {
        return service.getNovaAfericaoPlaca(codUnidade, placa, tipoAfericao, userToken);
    }

    @GET
    @Path("/unidades/{codUnidade}/nova-avulsa/{codPneu}")
    @Secured(permissions = Pilares.Frota.Afericao.REALIZAR)
    public NovaAfericaoAvulsa getNovaAfericaoAvulsa(@PathParam("codUnidade") @Required Long codUnidade,
                                                    @PathParam("codPneu") @Required Long codPneu,
                                                    @QueryParam("tipoAfericao") @Required String tipoAfericao) throws
            ProLogException {
        return service.getNovaAfericaoAvulsa(codUnidade, codPneu, tipoAfericao);
    }

    @GET
    @Path("/{codUnidade}/{codTipoVeiculo}/{placaVeiculo}")
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR,
            Pilares.Frota.Afericao.REALIZAR,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    public List<AfericaoPlaca> getAfericoesByCodUnidadeByPlaca(
            @PathParam("codUnidade") Long codUnidade,
            @PathParam("codTipoVeiculo") String codTipoVeiculo,
            @PathParam("placaVeiculo") String placaVeiculo,
            @QueryParam("dataInicial") long dataInicial,
            @QueryParam("dataFinal") long dataFinal,
            @QueryParam("limit") int limit,
            @QueryParam("offset") long offset,
            @HeaderParam("Authorization") String userToken) {
        return service.getAfericoes(
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
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR,
            Pilares.Frota.Afericao.REALIZAR,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/{codUnidade}/{codAfericao}")
    public AfericaoPlaca getByCod(@PathParam("codUnidade") Long codUnidade,
                                  @PathParam("codAfericao") Long codAfericao,
                                  @HeaderParam("Authorization") String userToken) {
        return service.getByCod(codUnidade, codAfericao, userToken);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Afericao.VISUALIZAR,
            Pilares.Frota.Afericao.REALIZAR,
            Pilares.Frota.OrdemServico.Pneu.VISUALIZAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM})
    @Path("/restricoes/{codUnidade}")
    public Restricao getRestricaoByCodUnidade(@PathParam("codUnidade") Long codUnidade) {
        return service.getRestricaoByCodUnidade(codUnidade);
    }
}