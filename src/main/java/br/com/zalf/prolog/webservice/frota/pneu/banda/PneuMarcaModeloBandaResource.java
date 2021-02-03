package br.com.zalf.prolog.webservice.frota.pneu.banda;

import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.network.metadata.Optional;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.frota.pneu.banda._model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 18/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("pneus/bandas")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class PneuMarcaModeloBandaResource {
    @NotNull
    private final PneuMarcaModeloBandaService service = new PneuMarcaModeloBandaService();

    //
    //
    // Métodos de marcas de banda.
    //
    //
    @POST
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    @Path("/marcas")
    public ResponseWithCod insertMarcaBanda(@Valid PneuMarcaBandaInsercao marcaBanda) {
        return service.insertMarcaBanda(marcaBanda);
    }

    @PUT
    @Secured(permissions = {
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE})
    @Path("/marcas")
    public ResponseWithCod updateMarcaBanda(@Valid PneuMarcaBandaEdicao marcaBanda) {
        return service.updateMarcaBanda(marcaBanda);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/marcas")
    @AppVersionCodeHandler(
            targetVersionCode = 89,
            versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
            actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
    public List<PneuMarcaBandaListagem> getListagemMarcasBanda(
            @QueryParam("codEmpresa") @Required Long codEmpresa,
            @QueryParam("comModelos") @Optional boolean comModelos,
            @QueryParam("incluirMarcasNaoUtilizadas") @DefaultValue("true") boolean incluirMarcasNaoUtilizadas) {
        return service.getListagemMarcasBanda(codEmpresa, comModelos, incluirMarcasNaoUtilizadas);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/marcas/{codMarca}")
    @AppVersionCodeHandler(
            targetVersionCode = 87,
            versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
            actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
    public PneuMarcaBandaVisualizacao getMarcaBanda(@PathParam("codMarca") Long codMarca) {
        return service.getMarcaBanda(codMarca);
    }

    //
    //
    // Métodos de modelos de banda.
    //
    //
    @POST
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    @Path("/modelos")
    public ResponseWithCod insertModeloBanda(@Valid final PneuModeloBandaInsercao pneuModeloBandaInsercao) {
        return service.insertModeloBanda(pneuModeloBandaInsercao);
    }

    @PUT
    @Secured(permissions = {
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE})
    @Path("/modelos")
    public ResponseWithCod updateModeloBanda(
            @HeaderParam("Authorization") final String userToken,
            @Required final PneuModeloBandaEdicao pneuModeloBandaEdicao)  {
        return service.updateModeloBanda(pneuModeloBandaEdicao);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/modelos")
    public List<PneuModeloBandaListagem> getListagemModelosBandas(@QueryParam("codEmpresa") Long codEmpresa,
                                                                  @QueryParam("codMarca") Long codMarca) {
        return service.getListagemModelosBandas(codEmpresa, codMarca);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_ANALISE,
            Pilares.Frota.Pneu.Movimentacao.MOVIMENTAR_DESCARTE,
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/modelos/{codModelo}")
    public PneuModeloBandaVisualizacao getModeloBanda(@PathParam("codModelo") Long codModelo) {
        return service.getModeloBanda(codModelo);
    }
}