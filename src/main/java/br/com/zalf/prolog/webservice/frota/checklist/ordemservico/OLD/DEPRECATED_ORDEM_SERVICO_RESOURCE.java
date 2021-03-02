package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OLD;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.VersaoAppBloqueadaException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 06/09/2018
 *DEPRECATED_ORDEM_SERVICO_RESOURCE
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/v2/checklist/ordemServico")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Deprecated
@AppVersionCodeHandler(
        targetVersionCode = 62,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class DEPRECATED_ORDEM_SERVICO_RESOURCE {
    private static final String ERROR_MESSAGE = "Atualize o aplicativo para utilizar esta funcionalidade";

    @POST
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/consertaItem/{placa}")
    @Secured(permissions = Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM)
    public Response consertaItem(ItemOrdemServico item,
                                 @PathParam("placa") String placa) throws ProLogException {
        throw new VersaoAppBloqueadaException(ERROR_MESSAGE);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/{codUnidade}/{tipoVeiculo}/{placa}/{status}")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public List<OrdemServico> getOs(@PathParam("codUnidade") Long codUnidade,
                                    @PathParam("tipoVeiculo") String tipoVeiculo,
                                    @PathParam("placa") String placa,
                                    @PathParam("status") String status,
                                    @QueryParam("limit") Integer limit,
                                    @QueryParam("offset") Long offset) throws ProLogException {
        throw new VersaoAppBloqueadaException(ERROR_MESSAGE);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/manutencao/{placa}/{status}/{prioridade}")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public List<ItemOrdemServico> getItensOsManutencaoHolder(@PathParam("placa") @Required String placa,
                                                             @PathParam("status") @Required String status,
                                                             @PathParam("prioridade") @Required String prioridade,
                                                             @QueryParam("limit") @Optional Integer limit,
                                                             @QueryParam("offset") @Optional Long offset)
            throws ProLogException {
        throw new VersaoAppBloqueadaException(ERROR_MESSAGE);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/itens")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public List<ItemOrdemServico> getItensOrdemServico(@QueryParam("placa") @Required String placa)
            throws ProLogException {
        throw new VersaoAppBloqueadaException(ERROR_MESSAGE);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/{codOs}/unidades/{codUnidade}/itens")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public List<ItemOrdemServico> getItensOrdemServico(@PathParam("codOs") @Required Long codOs,
                                                       @PathParam("codUnidade") @Required Long codUnidade,
                                                       @QueryParam("statusItemOs") @Optional String statusItemOs)
            throws ProLogException {
        throw new VersaoAppBloqueadaException(ERROR_MESSAGE);
    }

    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/manutencao/{codUnidade}")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    public List<ManutencaoHolder> getResumoManutencaoHolder(@PathParam("codUnidade") @Required Long codUnidade,
                                                            @QueryParam("codTipoVeiculo") @Optional Long codTipoVeiculo,
                                                            @QueryParam("placaVeiculo") @Optional String placaVeiculo,
                                                            @QueryParam("itensEmAberto") @Required Boolean itensEmAberto,
                                                            @QueryParam("limit") @Required int limit,
                                                            @QueryParam("offset") @Required int offset)
            throws ProLogException {
        throw new VersaoAppBloqueadaException(ERROR_MESSAGE);
    }


    /**
     * @deprecated at 2018-08-13. Use {@link #getResumoManutencaoHolder(Long, Long, String, Boolean, int, int)} instead.
     */
    @GET
    @UsedBy(platforms = Platform.ANDROID)
    @Path("/manutencao/{codUnidade}/{tipoVeiculo}/{placa}/{status}")
    @Secured(permissions = {
            Pilares.Frota.OrdemServico.Checklist.VISUALIZAR,
            Pilares.Frota.OrdemServico.Checklist.RESOLVER_ITEM})
    @Deprecated
    public List<ManutencaoHolder> getResumoManutencaoHolder(@PathParam("codUnidade") Long codUnidade,
                                                            @PathParam("tipoVeiculo") String codTipo,
                                                            @PathParam("placa") String placa,
                                                            @PathParam("status") String status,
                                                            @QueryParam("limit") int limit,
                                                            @QueryParam("offset") int offset) throws ProLogException {
        throw new VersaoAppBloqueadaException(ERROR_MESSAGE);
    }
}