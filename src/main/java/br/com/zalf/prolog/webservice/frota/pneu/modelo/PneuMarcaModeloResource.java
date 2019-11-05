package br.com.zalf.prolog.webservice.frota.pneu.modelo;


import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Optional;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.frota.pneu.modelo._model.*;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
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
@DebugLog
@Path("pneus")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class PneuMarcaModeloResource {
    @NotNull
    private final PneuMarcaModeloService service = new PneuMarcaModeloService();

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR, Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/marcas")
    public List<PneuMarcaListagem> getListagemMarcasPneu(
            @QueryParam("codEmpresa") @Required Long codEmpresa,
            @QueryParam("comModelos") @Optional boolean comModelos) {
        return service.getListagemMarcasPneu(codEmpresa, comModelos);
    }

    @POST
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR})
    @Path("/modelos")
    public ResponseWithCod insertModeloPneu(@Valid final PneuModeloInsercao pneuModeloInsercao) {
        return service.insertModeloPneu(pneuModeloInsercao);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR})
    @Path("/modelos")
    public ResponseWithCod updateModeloPneu(
            @HeaderParam("Authorization") final String userToken,
            @Valid final PneuModeloEdicao pneuModeloEdicao) {
        return service.updateModeloPneu(pneuModeloEdicao);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR, Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/modelos")
    public List<PneuModeloListagem> getListagemModelosPneu(@QueryParam("codEmpresa") Long codEmpresa,
                                                           @QueryParam("codMarca") Long codMarca) {
        return service.getListagemModelosPneu(codEmpresa, codMarca);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Pneu.CADASTRAR, Pilares.Frota.Pneu.ALTERAR, Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/modelos/{codModelo}")
    @AppVersionCodeHandler(
            targetVersionCode = 87,
            versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
            actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
    public PneuModeloVisualizacao getModeloPneu(@PathParam("codModelo") Long codModelo) {
        return service.getModeloPneu(codModelo);
    }
}