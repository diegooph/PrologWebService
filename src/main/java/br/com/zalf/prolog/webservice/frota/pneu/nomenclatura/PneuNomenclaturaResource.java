package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.model.PneuNomenclaturaItem;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.model.PneuNomenclaturaItemVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import java.util.List;

/**
 * Created on 10/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuNomenclaturaResource {
    private PneuNomenclaturaService service = new PneuNomenclaturaService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    @Path("/nomenclaturas-post")
    public Response insertOrUpdateNomenclatura(@Required final List<PneuNomenclaturaItem> pneuNomenclaturaItem,
                                               @HeaderParam("Authorization") @Required final String userToken) throws ProLogException {
        return service.insertOrUpdateNomenclatura(pneuNomenclaturaItem, userToken);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    @Path("/nomenclaturas-get")
    public List<PneuNomenclaturaItemVisualizacao> getPneuNomenclaturaItemVisualizacao(
            @QueryParam("codEmpresa") @Required final Long codEmpresa,
            @QueryParam("codDiagrama") @Required final Long codDiagrama) throws ProLogException {
        return service.getPneuNomenclaturaItemVisualizacao(codEmpresa, codDiagrama);
    }
}
