package br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico.model.PneuTipoServico;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import java.util.List;

/**
 * Created on 24/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/pneus/tipos-servicos")
public class PneuTipoServicoResource {

    private final PneuTipoServicoService service = new PneuTipoServicoService();

    @POST
    @Secured(permissions = {Pilares.Frota.Recapadora.TipoServico.CADASTRO})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    public AbstractResponse insert(@HeaderParam("Authorization") @Required String token,
                                   @Required PneuTipoServico tipoServico) throws Throwable {
        return service.insertPneuTipoServico(token, tipoServico);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Recapadora.TipoServico.EDICAO})
    @UsedBy(platforms = {Platform.WEBSITE})
    @Path("/{codEmpresa}")
    public Response atualizaPneuTipoServico(@HeaderParam("Authorization") @Required String token,
                                            @PathParam("codEmpresa") @Required Long codEmpresa,
                                            @Required PneuTipoServico tipoServico) throws Throwable {
        return service.atualizaPneuTipoServico(token, codEmpresa, tipoServico);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Recapadora.TipoServico.VISUALIZACAO})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/{codEmpresa}")
    public List<PneuTipoServico> getPneuTiposServicos(
            @PathParam("codEmpresa") @Required Long codEmpresa,
            @QueryParam("ativas") @Required Boolean ativas) throws Throwable {
        return service.getPneuTiposServicos(codEmpresa, ativas);
    }

    @GET
    @Secured(permissions = {Pilares.Frota.Recapadora.TipoServico.VISUALIZACAO})
    @UsedBy(platforms = {Platform.WEBSITE})
    @Path("/{codEmpresa}/{codTipoServico}")
    public PneuTipoServico getPneuTipoServico(
            @PathParam("codEmpresa") @Required Long codEmpresa,
            @PathParam("codTipoServico") @Required Long codTipoServico) throws Throwable {
        return service.getPneuTipoServico(codEmpresa, codTipoServico);
    }

    @PUT
    @Secured(permissions = {Pilares.Frota.Recapadora.TipoServico.EDICAO})
    @UsedBy(platforms = Platform.WEBSITE)
    @Path("/{codEmpresa}/status")
    public Response alterarStatusPneuTipoServico(@HeaderParam("Authorization") @Required String token,
                                                 @PathParam("codEmpresa") @Required Long codEmpresa,
                                                 @Required PneuTipoServico tipoServico) throws Throwable {
        return service.alterarStatusPneuTipoServico(token, codEmpresa, tipoServico);
    }
}
