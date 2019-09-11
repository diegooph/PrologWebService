package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.model.PneuNomenclaturaItem;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.model.PneuNomenclaturaItemVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.AppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.DefaultAppVersionCodeHandler;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionCodeHandlerMode;
import br.com.zalf.prolog.webservice.interceptors.versioncodebarrier.VersionNotPresentAction;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 10/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/pneus-nomenclaturas")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@AppVersionCodeHandler(
        implementation = DefaultAppVersionCodeHandler.class,
        targetVersionCode = 64,
        versionCodeHandlerMode = VersionCodeHandlerMode.BLOCK_THIS_VERSION_AND_BELOW,
        actionIfVersionNotPresent = VersionNotPresentAction.BLOCK_ANYWAY)
public class PneuNomenclaturaResource {
    private PneuNomenclaturaService service = new PneuNomenclaturaService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    @Path("/post")
    public Response insertOrUpdateNomenclatura(@Required final List<PneuNomenclaturaItem> pneuNomenclaturaItem,
                                               @HeaderParam("Authorization") @Required final String userToken) throws ProLogException {
        return service.insertOrUpdateNomenclatura(pneuNomenclaturaItem, userToken);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/get")
    public List<PneuNomenclaturaItemVisualizacao> getPneuNomenclaturaItemVisualizacao(
            @QueryParam("codEmpresa") @Required final Long codEmpresa,
            @QueryParam("codDiagrama") @Required final Long codDiagrama) throws ProLogException {
        return service.getPneuNomenclaturaItemVisualizacao(codEmpresa, codDiagrama);
    }
}
