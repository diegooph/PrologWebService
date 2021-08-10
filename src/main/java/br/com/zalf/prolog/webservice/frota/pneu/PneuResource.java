package br.com.zalf.prolog.webservice.frota.pneu;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.PrologCustomHeaders;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.PneuRetornoDescarte;
import br.com.zalf.prolog.webservice.frota.pneu._model.PneuRetornoDescarteResponse;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.debug.ConsoleDebugLog;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@ConsoleDebugLog
@Path("/v2/pneus")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        implementation = DefaultAppVersionCodeHandler.class,
        targetVersionCode = 64,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public final class PneuResource {
    private final PneuService service = new PneuService();

    @Inject
    private Provider<ColaboradorAutenticado> colaboradorAutenticadoProvider;

    @POST
    @Secured
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public List<Long> insert(
            @HeaderParam("Authorization") @Required final String userToken,
            @FormDataParam("file") @Required final InputStream fileInputStream) throws ProLogException {
        return service.insert(colaboradorAutenticadoProvider.get().getCodigo(), userToken, fileInputStream);
    }

    @POST
    @Secured(permissions = Pilares.Frota.Pneu.CADASTRAR)
    @Path("/{codUnidade}")
    public AbstractResponse insert(@HeaderParam("Authorization") @Required final String userToken,
                                   @HeaderParam(PrologCustomHeaders.AppVersionAndroid.PROLOG_APP_VERSION)
                                   @Required final Integer appVersion,
                                   @PathParam("codUnidade") @Required final Long codUnidade,
                                   @QueryParam("ignoreDotValidation") final boolean ignoreDotValidation,
                                   @Required final Pneu pneu) throws ProLogException {
        return service.insert(
                colaboradorAutenticadoProvider.get().getCodigo(),
                userToken,
                codUnidade,
                pneu,
                appVersion != null ? OrigemAcaoEnum.PROLOG_ANDROID : OrigemAcaoEnum.PROLOG_WEB,
                ignoreDotValidation);
    }

    @POST
    @Secured(permissions = Pilares.Frota.Pneu.ALTERAR)
    @Path("/retornar-descarte")
    public PneuRetornoDescarteResponse retornarPneuDescarte(
            @NotNull @Required final PneuRetornoDescarte pneuRetornoDescarte) {
        return service.retornarPneuDescarte(pneuRetornoDescarte);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR})
    @Path("/{codUnidade}/{codPneuOriginal}")
    public Response update(
            @HeaderParam("Authorization") @Required final String userToken,
            @PathParam("codUnidade") @Required final Long codUnidade,
            @PathParam("codPneuOriginal") @Required final Long codOriginalPneu,
            @Required final Pneu pneu) throws ProLogException {
        return service.update(colaboradorAutenticadoProvider.get().getCodigo(), userToken, codUnidade, codOriginalPneu, pneu);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.VISUALIZAR,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    @Path("/{codUnidade}/{status}")
    @AppVersionCodeHandler(
            targetVersionCode = 68,
            versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
            actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
    public List<Pneu> getPneuByCodUnidadeByStatus(@HeaderParam("Authorization") @Required final String userToken,
                                                  @PathParam("codUnidade") final Long codUnidade,
                                                  @PathParam("status") final String status) throws ProLogException {
        return service.getPneusByCodUnidadesByStatus(userToken, Collections.singletonList(codUnidade), status);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.VISUALIZAR,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.OrdemServico.Pneu.CONSERTAR_ITEM,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_VEICULO_ESTOQUE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE})
    @Path("/listagem")
    @AppVersionCodeHandler(
            targetVersionCode = 68,
            versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
            actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
    public List<Pneu> getPneuByCodUnidadesByStatus(@HeaderParam("Authorization") @Required final String userToken,
                                                   @QueryParam("codUnidades") @Required final List<Long> codUnidades,
                                                   @QueryParam("status") @Required final String status) {
        return service.getPneusByCodUnidadesByStatus(userToken, codUnidades, status);
    }

    @GET
    @Secured
    @Path("/unidades/{codUnidade}/{codPneu}")
    public Pneu getPneuByCod(@PathParam("codPneu") final Long codPneu,
                             @PathParam("codUnidade") final Long codUnidade) throws ProLogException {
        return service.getPneuByCod(codPneu, codUnidade);
    }

    @PUT
    @Secured
    @Path("/{codPneu}/fotos-cadastro/sincronizada")
    public Response marcarFotoComoSincronizada(@PathParam("codPneu") @Required final Long codPneu,
                                               @QueryParam("urlFotoPneu") @Required final String urlFotoPneu) {
        service.marcarFotoComoSincronizada(codPneu, urlFotoPneu);
        return Response.ok("Foto marcada como sincronizada com sucesso");
    }
}