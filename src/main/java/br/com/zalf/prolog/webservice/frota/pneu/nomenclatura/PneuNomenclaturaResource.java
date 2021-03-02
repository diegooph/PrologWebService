package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura;

import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.metadata.Platform;
import br.com.zalf.prolog.webservice.commons.network.metadata.Required;
import br.com.zalf.prolog.webservice.commons.network.metadata.UsedBy;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model.PneuNomenclaturaCadastro;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura._model.PneuNomenclaturaItemVisualizacao;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created on 10/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Path("/v2/pneus/nomenclaturas")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class PneuNomenclaturaResource {
    @NotNull
    private final PneuNomenclaturaService service = new PneuNomenclaturaService();

    @POST
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR})
    public Response insertOrUpdateNomenclatura(@Required final PneuNomenclaturaCadastro pneuNomenclaturaCadastro,
                                               @HeaderParam("Authorization") @Required final String userToken)
            throws ProLogException {
        return service.insertOrUpdateNomenclatura(pneuNomenclaturaCadastro, userToken);
    }

    @GET
    @Secured(permissions = {
            Pilares.Frota.Pneu.CADASTRAR,
            Pilares.Frota.Pneu.ALTERAR,
            Pilares.Frota.Pneu.VISUALIZAR})
    public List<PneuNomenclaturaItemVisualizacao> getPneuNomenclaturaItemVisualizacao(
            @QueryParam("codEmpresa") @Required final Long codEmpresa,
            @QueryParam("codDiagrama") @Required final Long codDiagrama) throws ProLogException {
        return service.getPneuNomenclaturaItemVisualizacao(codEmpresa, codDiagrama);
    }
}