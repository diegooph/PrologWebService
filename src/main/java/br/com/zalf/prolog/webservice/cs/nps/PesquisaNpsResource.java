package br.com.zalf.prolog.webservice.cs.nps;

import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Platform;
import br.com.zalf.prolog.webservice.commons.util.Required;
import br.com.zalf.prolog.webservice.commons.util.UsedBy;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsBloqueio;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsRealizada;
import br.com.zalf.prolog.webservice.interceptors.auth.Secured;
import br.com.zalf.prolog.webservice.interceptors.log.DebugLog;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created on 2019-10-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Path("/cs/nps")
@DebugLog
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public final class PesquisaNpsResource {
    @NotNull
    private final PesquisaNpsService service = new PesquisaNpsService();

    @GET
    @Secured
    @UsedBy(platforms = Platform.WEBSITE)
    public Response getPesquisaNpsColaborador(@QueryParam("codColaborador") @Required final Long codColaborador) {
        return service.getPesquisaNpsColaborador(codColaborador);
    }

    @POST
    @Secured
    @UsedBy(platforms = Platform.WEBSITE)
    public ResponseWithCod insereRespostasPesquisaNps(@Required final PesquisaNpsRealizada pesquisaRealizada) {
        return service.insertRespostasPesquisaNps(pesquisaRealizada);
    }

    @POST
    @Secured
    @Path("/bloqueio")
    @UsedBy(platforms = Platform.WEBSITE)
    public void bloqueiaPesquisaNpsColaborador(@Required final PesquisaNpsBloqueio pesquisaBloqueio) {
        service.bloqueiaPesquisaNpsColaborador(pesquisaBloqueio);
    }
}