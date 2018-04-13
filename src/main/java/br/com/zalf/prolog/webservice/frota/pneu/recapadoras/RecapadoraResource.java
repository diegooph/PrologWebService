package br.com.zalf.prolog.webservice.frota.pneu.recapadoras;

import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;

import javax.ws.rs.*;
import java.util.List;

/**
 * Created on 13/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Path("/recapadoras")
public class RecapadoraResource {

    private final RecapadoraService service = new RecapadoraService();

    @POST
    @Secured(permissions = {})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    public void insert(@HeaderParam("Authorization") @Required String token,
                       @Required Recapadora recapadora) throws Exception {
        service.insertRecapadora(token, recapadora);
    }

    @PUT
    @Secured(permissions = {})
    @UsedBy(platforms = {Platform.WEBSITE})
    @Path("/{codEmpresa}")
    public void atualizaRecapadoras(@PathParam("codEmpresa") @Required Long codEmpresa,
                                    @Required Recapadora recapadora) throws Exception {
        service.atualizaRecapadoras(codEmpresa, recapadora);
    }

    @GET
    @Secured(permissions = {})
    @UsedBy(platforms = {Platform.ANDROID, Platform.WEBSITE})
    @Path("/{codEmpresa}")
    public List<Recapadora> getRecapadoras(@PathParam("codEmpresa") @Required Long codEmpresa,
                                           @QueryParam("ativas") @Required Boolean ativas) throws Exception {
        return service.getRecapadoras(codEmpresa, ativas);
    }

    @PUT
    @UsedBy(platforms = Platform.WEBSITE)
    @Secured(permissions = {})
    @Path("/{codEmpresa}/status")
    public void alterarStatusRecapadoras(@HeaderParam("Authorization") @Required String token,
                                         @PathParam("codEmpresa") @Required Long codEmpresa,
                                         @Required List<Recapadora> recapadoras) throws Exception {
        service.alterarStatusRecapadoras(token, codEmpresa, recapadoras);
    }
}
