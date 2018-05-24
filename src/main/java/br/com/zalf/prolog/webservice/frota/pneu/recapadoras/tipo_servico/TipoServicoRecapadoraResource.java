package br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico;

import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;

import javax.ws.rs.*;
import java.util.List;

/**
 * Created on 24/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/recapadoras/servicos")
public class TipoServicoRecapadoraResource {

    private final TipoServicoRecapadoraService service = new TipoServicoRecapadoraService();

    @POST
    @Secured(permissions = {})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    public AbstractResponse insert(@HeaderParam("Authorization") @Required String token,
                                   @Required TipoServicoRecapadora tipoServico) throws Exception {
        return service.insertTipoServicoRecapadora(token, tipoServico);
    }

    @PUT
    @Secured(permissions = {})
    @UsedBy(platforms = {Platform.WEBSITE})
    @Path("/{codEmpresa}")
    public Response atualizaTipoServicoRecapadora(@HeaderParam("Authorization") @Required String token,
                                                  @PathParam("codEmpresa") @Required Long codEmpresa,
                                                  @Required TipoServicoRecapadora tipoServico) throws Exception {
        return service.atualizaTipoServicoRecapadora(token, codEmpresa, tipoServico);
    }

    @GET
    @Secured(permissions = {})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/{codEmpresa}")
    public List<TipoServicoRecapadora> getTiposServicosRecapadora(
            @PathParam("codEmpresa") @Required Long codEmpresa,
            @QueryParam("ativas") @Required Boolean ativas) throws Exception {
        return service.getTiposServicosRecapadora(codEmpresa, ativas);
    }

    @GET
    @Secured(permissions = {})
    @UsedBy(platforms = {Platform.WEBSITE})
    @Path("/{codEmpresa}/{codTipoServico}")
    public TipoServicoRecapadora getTipoServicoRecapadora(
            @PathParam("codEmpresa") @Required Long codEmpresa,
            @PathParam("codTipoServico") @Required Long codTipoServico) throws Exception {
        return service.getTipoServicoRecapadora(codEmpresa, codTipoServico);
    }

    @PUT
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {Pilares.Frota.Recapadora.EDICAO})
    @Path("/{codEmpresa}/status")
    public Response alterarStatusTipoServicoRecapadoras(@HeaderParam("Authorization") @Required String token,
                                                        @PathParam("codEmpresa") @Required Long codEmpresa,
                                                        @Required TipoServicoRecapadora tipoServico) throws Exception {
        return service.alterarStatusTipoServicoRecapadora(token, codEmpresa, tipoServico);
    }
}
